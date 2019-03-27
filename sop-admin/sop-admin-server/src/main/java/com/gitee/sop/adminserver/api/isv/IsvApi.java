package com.gitee.sop.adminserver.api.isv;

import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import com.gitee.easyopen.util.CopyUtil;
import com.gitee.easyopen.util.KeyStore;
import com.gitee.easyopen.util.RSAUtil;
import com.gitee.fastmybatis.core.PageInfo;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import com.gitee.sop.adminserver.api.isv.param.IsvInfoFormAdd;
import com.gitee.sop.adminserver.api.isv.param.IsvInfoFormUpdate;
import com.gitee.sop.adminserver.api.isv.param.IsvPageParam;
import com.gitee.sop.adminserver.api.isv.result.AppKeySecretVo;
import com.gitee.sop.adminserver.api.isv.result.IsvVO;
import com.gitee.sop.adminserver.api.isv.result.PubPriVo;
import com.gitee.sop.adminserver.api.isv.result.RoleVO;
import com.gitee.sop.adminserver.common.IdGen;
import com.gitee.sop.adminserver.entity.IsvInfo;
import com.gitee.sop.adminserver.entity.PermIsvRole;
import com.gitee.sop.adminserver.entity.PermRole;
import com.gitee.sop.adminserver.mapper.IsvInfoMapper;
import com.gitee.sop.adminserver.mapper.PermIsvRoleMapper;
import com.gitee.sop.adminserver.mapper.PermRoleMapper;
import com.gitee.sop.adminserver.service.PermService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
public class IsvApi {

    @Autowired
    IsvInfoMapper isvInfoMapper;

    @Autowired
    PermIsvRoleMapper permIsvRoleMapper;

    @Autowired
    PermRoleMapper permRoleMapper;

    @Autowired
    PermService permService;

    @Api(name = "isv.info.page")
    @ApiDocMethod(description = "接入方列表")
    PageEasyui pageIsv(IsvPageParam param) {
        Query query = Query.build(param);
        PageInfo<IsvInfo> pageInfo = MapperUtil.query(isvInfoMapper, query);
        List<IsvInfo> list = pageInfo.getList();

        List<IsvVO> retList = list.stream()
                .map(isvInfo -> {
                    IsvVO vo = new IsvVO();
                    CopyUtil.copyProperties(isvInfo, vo);
                    vo.setRoleList(this.buildIsvRole(isvInfo));
                    return vo;
                })
                .collect(Collectors.toList());

        PageEasyui<IsvVO> pageInfoRet = new PageEasyui<>();
        pageInfoRet.setTotal(pageInfo.getTotal());
        pageInfoRet.setList(retList);

        return pageInfoRet;
    }

    /**
     * 构建ISV拥有的角色
     *
     * @param permClient
     * @return
     */
    List<RoleVO> buildIsvRole(IsvInfo permClient) {
        List<String> roleCodeList = permService.listClientRoleCode(permClient.getId());
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
    void addIsv(IsvInfoFormAdd param) {
        IsvInfo rec = new IsvInfo();
        CopyUtil.copyPropertiesIgnoreNull(param, rec);
        isvInfoMapper.saveIgnoreNull(rec);

        this.saveClientRole(rec, param.getRoleCode());
        // TODO:发送消息队列到zookeeper
    }

    @Api(name = "isv.info.update")
    @ApiDocMethod(description = "修改isv")
    void updateIsv(IsvInfoFormUpdate param) {
        IsvInfo rec = isvInfoMapper.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, rec);
        isvInfoMapper.updateIgnoreNull(rec);

        this.saveClientRole(rec, param.getRoleCode());

//        syncService.syncAppSecretConfig(Sets.newHashSet(param.getApp()));
        // TODO:发送消息队列到zookeeper
    }

    @Api(name = "isv.pubprikey.create")
    @ApiDocMethod(description = "生成公私钥")
    PubPriVo createPubPriKey() throws Exception {
        KeyStore keyStore = RSAUtil.createKeys();
        PubPriVo vo = new PubPriVo();
        vo.setPubKey(keyStore.getPublicKey());
        vo.setPriKey(keyStore.getPrivateKey());
        return vo;
    }

    @Api(name = "isv.appkeysecret.create")
    @ApiDocMethod(description = "生成appkey")
    AppKeySecretVo createAppKeySecret() {
        String appKey = new SimpleDateFormat("yyyyMMdd").format(new Date()) + IdGen.nextId();
        String secret = IdGen.uuid();
        AppKeySecretVo vo = new AppKeySecretVo();
        vo.setAppKey(appKey);
        vo.setSecret(secret);
        return vo;
    }

    void saveClientRole(IsvInfo isvInfo, List<String> roleCodeList) {
        Query query = new Query();
        long isvInfoId = isvInfo.getId();
        query.eq("isv_info_id", isvInfoId);
        permIsvRoleMapper.deleteByQuery(query);

        List<PermIsvRole> tobeSaveList = roleCodeList.stream()
                .map(roleCode -> {
                    PermIsvRole rec = new PermIsvRole();
                    rec.setIsvInfoId(isvInfoId);
                    rec.setRoleCode(roleCode);
                    return rec;
                })
                .collect(Collectors.toList());

        permIsvRoleMapper.saveBatch(tobeSaveList);
    }
}
