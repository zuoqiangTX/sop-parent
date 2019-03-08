package com.gitee.sop.gatewaycommon.manager;

/**
 * @author tanghc
 */
public interface RouteRepository<R, T> {
    T get(String id);

    String add(R serviceRouteInfo, T route);

    void delete(String id);

    void deleteAll(R serviceRouteInfo);
}
