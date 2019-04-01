package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.IsvRoutePermission;

/**
 * @author tanghc
 */
public interface RoutePermissionManager {
    void load();

    void update(IsvRoutePermission isvRoutePermission);
}
