package com.gitee.sop.gatewaycommon.bean;

/**
 * @author tanghc
 */
public abstract class AbstractTargetRoute<T> implements TargetRoute<T> {

    //服务信息
    private ServiceRouteInfo serviceRouteInfo;
    //微服务路由对象（具体的接口名称）
    private RouteDefinition routeDefinition;
    //网关路由对象
    private T targetRoute;

    public AbstractTargetRoute(ServiceRouteInfo serviceRouteInfo, RouteDefinition routeDefinition, T targetRoute) {
        this.serviceRouteInfo = serviceRouteInfo;
        this.routeDefinition = routeDefinition;
        this.targetRoute = targetRoute;
    }

    @Override
    public ServiceRouteInfo getServiceRouteInfo() {
        return serviceRouteInfo;
    }

    @Override
    public RouteDefinition getRouteDefinition() {
        return routeDefinition;
    }

    @Override
    public T getTargetRouteDefinition() {
        return targetRoute;
    }
}
