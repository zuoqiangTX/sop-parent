package com.gitee.sop.gatewaycommon.bean;

/**
 * @author tanghc
 */
public interface TargetRoute<R extends BaseServiceRouteInfo, E extends BaseRouteDefinition, T> {
    R getServiceRouteInfo();

    E getRouteDefinition();

    T getTargetRouteDefinition();
}
