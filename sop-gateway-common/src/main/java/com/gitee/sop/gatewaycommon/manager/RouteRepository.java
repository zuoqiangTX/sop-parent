package com.gitee.sop.gatewaycommon.manager;

/**
 * @author tanghc
 */
public interface RouteRepository<T> {
    T get(String id);

    String add(T route);

    String update(T route);

    void delete(String id);
}
