package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.TargetRoute;

/**
 * @author tanghc
 */
public interface RouteRepository<T extends TargetRoute> {
    /**
     * 获取路由信息
     * @param id 路由id
     * @return
     */
    T get(String id);

    /**
     * 添加路由
     * @param targetRoute
     * @return
     */
    String add(T targetRoute);

    /**
     * 更新路由
     * @param targetRoute
     */
    void update(T targetRoute);

    /**
     * 删除路由
     * @param id 路由id
     */
    void delete(String id);

    /**
     * 删除service下的所有路由
     * @param serviceId
     */
    void deleteAll(String serviceId);
}
