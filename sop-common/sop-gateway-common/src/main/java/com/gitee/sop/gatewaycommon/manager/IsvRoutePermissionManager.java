package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.IsvRoutePermission;

/**
 * @author tanghc
 */
public interface IsvRoutePermissionManager {

    /**
     * 加载路由权限信息
     */
    void load();

    /**
     * 加载权限
     * @param isvRoutePermission
     */
    void update(IsvRoutePermission isvRoutePermission);

    /**
     * 判断是否有权限
     * @param appKey
     * @param routeId
     * @return
     */
    boolean hasPermission(String appKey, String routeId);

    /**
     * 删除权限
     * @param appKey
     */
    void remove(String appKey);
}
