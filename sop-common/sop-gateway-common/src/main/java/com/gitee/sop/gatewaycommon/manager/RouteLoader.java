package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ServiceRouteInfo;

/**
 * @author tanghc
 */
public interface RouteLoader {
    void load(ServiceRouteInfo serviceRouteInfo);

    void remove(String serviceId);
}
