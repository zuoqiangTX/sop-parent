package com.gitee.sop.adminserver.service;

import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.sop.adminserver.bean.ChannelMsg;
import com.gitee.sop.adminserver.bean.IsvRoutePermission;
import com.gitee.sop.adminserver.bean.ZookeeperContext;
import com.gitee.sop.adminserver.entity.PermIsvRole;
import com.gitee.sop.adminserver.entity.PermRolePermission;
import com.gitee.sop.adminserver.mapper.IsvInfoMapper;
import com.gitee.sop.adminserver.mapper.PermIsvRoleMapper;
import com.gitee.sop.adminserver.mapper.PermRolePermissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 路由权限业务类
 *
 * @author thc
 */
@Service
@Slf4j
public class RoutePermissionService {

    @Autowired
    IsvInfoMapper isvInfoMapper;

    @Autowired
    PermIsvRoleMapper permIsvRoleMapper;

    @Autowired
    PermRolePermissionMapper permRolePermissionMapper;

    /**
     * 获取客户端角色码列表
     *
     * @param isvId
     * @return
     */
    public List<String> listClientRoleCode(long isvId) {
        List<PermIsvRole> list = permIsvRoleMapper.listByColumn("isv_id", isvId);
        return list.stream()
                .map(PermIsvRole::getRoleCode)
                .collect(Collectors.toList());
    }

    /**
     * 推送isv路由权限到zookeeper
     *
     * @param appKey
     * @param roleCodeList
     */
    public void sendIsvRolePermissionToZookeeper(String appKey, List<String> roleCodeList) throws Exception {
        Collections.sort(roleCodeList);
        List<String> routeIdList = this.getRouteIdList(roleCodeList);
        String roleCodeListMd5 = DigestUtils.md5Hex(JSON.toJSONString(routeIdList));
        IsvRoutePermission isvRoutePermission = new IsvRoutePermission();
        isvRoutePermission.setAppKey(appKey);
        isvRoutePermission.setRouteIdList(routeIdList);
        isvRoutePermission.setRouteIdListMd5(roleCodeListMd5);
        ChannelMsg channelMsg = new ChannelMsg("update", isvRoutePermission);
        String jsonData = JSON.toJSONString(channelMsg);
        String path = ZookeeperContext.getIsvRoutePermissionChannelPath();
        log.info("消息推送--路由权限(update), path:{}, data:{}", path, jsonData);
        ZookeeperContext.createOrUpdateData(path, jsonData);
    }

    /**
     * 获取角色对应的路由
     *
     * @param roleCodeList
     * @return
     */
    public List<String> getRouteIdList(List<String> roleCodeList) {
        if (CollectionUtils.isEmpty(roleCodeList)) {
            return Collections.emptyList();
        }
        Query query = new Query();
        query.in("role_code", roleCodeList);
        List<PermRolePermission> rolePermissionList = permRolePermissionMapper.list(query);
        return rolePermissionList.stream()
                .map(PermRolePermission::getRouteId)
                .collect(Collectors.toList());
    }

    /**
     * 推送所有路由权限到zookeeper
     */
    public void sendRoutePermissionReloadMsg() throws Exception {
        ChannelMsg channelMsg = new ChannelMsg("reload", null);
        String jsonData = JSON.toJSONString(channelMsg);
        String path = ZookeeperContext.getIsvRoutePermissionChannelPath();
        log.info("消息推送--路由权限(reload), path:{}, data:{}", path, jsonData);
        ZookeeperContext.createOrUpdateData(path, jsonData);
    }

}
