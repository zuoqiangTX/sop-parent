package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ServiceRouteInfo;

import java.util.function.Consumer;

/**
 * @author tanghc
 */
public interface RouteLoader {
    void load(ServiceRouteInfo serviceRouteInfo, Consumer<Object> callback);

    void remove(String serviceId);
}
