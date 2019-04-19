package com.gitee.sop.adminserver.api.isv;

import com.alibaba.fastjson.JSON;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import com.gitee.easyopen.exception.ApiException;
import com.gitee.easyopen.util.CopyUtil;
import com.gitee.easyopen.util.KeyStore;
import com.gitee.easyopen.util.RSAUtil;
import com.gitee.fastmybatis.core.PageInfo;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.util.MapperUtil;
import com.gitee.sop.adminserver.api.IdParam;
import com.gitee.sop.adminserver.api.isv.param.IsvInfoForm;
import com.gitee.sop.adminserver.api.isv.param.IsvInfoFormAdd;
import com.gitee.sop.adminserver.api.isv.param.IsvInfoFormUpdate;
import com.gitee.sop.adminserver.api.isv.param.IsvPageParam;
import com.gitee.sop.adminserver.api.isv.result.IsvFormVO;
import com.gitee.sop.adminserver.api.isv.result.IsvVO;
import com.gitee.sop.adminserver.api.isv.result.RoleVO;
import com.gitee.sop.adminserver.bean.ChannelMsg;
import com.gitee.sop.adminserver.bean.ZookeeperContext;
import com.gitee.sop.adminserver.common.IdGen;
import com.gitee.sop.adminserver.entity.IsvInfo;
import com.gitee.sop.adminserver.entity.PermIsvRole;
import com.gitee.sop.adminserver.entity.PermRole;
import com.gitee.sop.adminserver.mapper.IsvInfoMapper;
import com.gitee.sop.adminserver.mapper.PermIsvRoleMapper;
import com.gitee.sop.adminserver.mapper.PermRoleMapper;
import com.gitee.sop.adminserver.service.RoutePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc("ISV管理")
@Slf4j
public class IsvApi {

    public static final int SIGN_TYPE_RSA2 = 1;
    @Autowired
    IsvInfoMapper isvInfoMapper;

    @Autowired
    PermIsvRoleMapper permIsvRoleMapper;

    @Autowired
    PermRoleMapper permRoleMapper;

    @Autowired
    RoutePermissionService routePermissionService;

    @Api(name = "isv.info.page")
    @ApiDocMethod(description = "isv列表", results = {
            @ApiDocField(name = "pageIndex", description = "第几页", dataType = DataType.INT, example = "1"),
            @ApiDocField(name = "pageSize", description = "每页几条数据", dataType = DataType.INT, example = "10"),
            @ApiDocField(name = "total", description = "每页几条数据", dataType = DataType.LONG, example = "100"),
            @ApiDocField(name = "rows", description = "数据", dataType = DataType.ARRAY, elementClass = IsvVO.class)
    })
    PageInfo<IsvVO> pageIsv(IsvPageParam param) {
        Query query = Query.build(param);
        PageInfo<IsvInfo> pageInfo = MapperUtil.query(isvInfoMapper, query);
        List<IsvInfo> list = pageInfo.getList();

        List<IsvVO> retList = list.stream()
                .map(isvInfo -> {
                    return buildIsvVO(isvInfo);
                })
                .collect(Collectors.toList());

        PageInfo<IsvVO> pageInfoRet = new PageInfo<>();
        pageInfoRet.setTotal(pageInfo.getTotal());
        pageInfoRet.setList(retList);

        return pageInfoRet;
    }

    @Api(name = "isv.info.get")
    @ApiDocMethod(description = "获取isv")
    IsvVO getIsvVO(IdParam param) {
        IsvInfo isvInfo = isvInfoMapper.getById(param.getId());
        return buildIsvVO(isvInfo);
    }

    private IsvVO buildIsvVO(IsvInfo isvInfo) {
        if (isvInfo == null) {
            return null;
        }
        IsvVO vo = new IsvVO();
        CopyUtil.copyProperties(isvInfo, vo);
        vo.setRoleList(this.buildIsvRole(isvInfo));
        return vo;
    }

    /**
     * 构建ISV拥有的角色
     *
     * @param permClient
     * @return
     */
    List<RoleVO> buildIsvRole(IsvInfo permClient) {
        List<String> roleCodeList = routePermissionService.listClientRoleCode(permClient.getId());
        if (CollectionUtils.isEmpty(roleCodeList)) {
            return Collections.emptyList();
        }
        List<PermRole> list = permRoleMapper.list(new Query().in("role_code", roleCodeList));

        return list.stream()
                .map(permRole -> {
                    RoleVO vo = new RoleVO();
                    CopyUtil.copyProperties(permRole, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Api(name = "isv.info.add")
    @ApiDocMethod(description = "添加isv")
    @Transactional(rollbackFor = Exception.class)
    public void addIsv(IsvInfoFormAdd param) throws Exception {
        if (isvInfoMapper.getByColumn("app_key", param.getAppKey()) != null) {
            throw new ApiException("appKey已存在");
        }
        formatForm(param);
        IsvInfo rec = new IsvInfo();
        CopyUtil.copyPropertiesIgnoreNull(param, rec);
        isvInfoMapper.saveIgnoreNull(rec);
        if (CollectionUtils.isNotEmpty(param.getRoleCode())) {
            this.saveIsvRole(rec, param.getRoleCode());
        }

        this.sendChannelMsg(rec);
    }

    @Api(name = "isv.info.update")
    @ApiDocMethod(description = "修改isv")
    @Transactional(rollbackFor = Exception.class)
    public void updateIsv(IsvInfoFormUpdate param) {
        formatForm(param);
        IsvInfo rec = isvInfoMapper.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, rec);
        isvInfoMapper.updateIgnoreNull(rec);
        this.saveIsvRole(rec, param.getRoleCode());

        this.sendChannelMsg(rec);
    }

    private void formatForm(IsvInfoForm form) {
        if (form.getSignType() == SIGN_TYPE_RSA2) {
            form.setSecret("");
        } else {
            form.setPubKey("");
            form.setPriKey("");
        }
    }

    private void sendChannelMsg(IsvInfo rec) {
        ChannelMsg channelMsg = new ChannelMsg("update", rec);
        String path = ZookeeperContext.getIsvInfoChannelPath();
        String data = JSON.toJSONString(channelMsg);
        try {
            log.info("消息推送--ISV信息(update), path:{}, data:{}", path, data);
            ZookeeperContext.updatePathData(path, data);
        } catch (Exception e) {
            log.error("发送isvChannelMsg失败, path:{}, msg:{}", path, data, e);
            throw new ApiException("保存失败，请查看日志");
        }
    }

    @Api(name = "isv.form.gen")
    @ApiDocMethod(description = "isv表单内容一键生成")
    IsvFormVO createIsvForm() throws Exception {
        IsvFormVO isvFormVO = new IsvFormVO();
        String appKey = new SimpleDateFormat("yyyyMMdd").format(new Date()) + IdGen.nextId();
        String secret = IdGen.uuid();

        isvFormVO.setAppKey(appKey);
        isvFormVO.setSecret(secret);

        KeyStore keyStore = RSAUtil.createKeys();
        isvFormVO.setPubKey(keyStore.getPublicKey());
        isvFormVO.setPriKey(keyStore.getPrivateKey());
        return isvFormVO;
    }



    void saveIsvRole(IsvInfo isvInfo, List<String> roleCodeList) {
        Query query = new Query();
        long isvInfoId = isvInfo.getId();
        query.eq("isv_id", isvInfoId);
        permIsvRoleMapper.deleteByQuery(query);

        List<PermIsvRole> tobeSaveList = roleCodeList.stream()
                .map(roleCode -> {
                    PermIsvRole rec = new PermIsvRole();
                    rec.setIsvId(isvInfoId);
                    rec.setRoleCode(roleCode);
                    return rec;
                })
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(tobeSaveList)) {
            permIsvRoleMapper.saveBatch(tobeSaveList);
        }

        try {
            routePermissionService.sendIsvRolePermissionToZookeeper(isvInfo.getAppKey(), roleCodeList);
        } catch (Exception e) {
            log.error("保存到zookeeper中失败，isvInfo:{}, roleCodeList:{}", isvInfo, roleCodeList);
            throw new ApiException("保存失败，请查看日志");
        }
    }
}
