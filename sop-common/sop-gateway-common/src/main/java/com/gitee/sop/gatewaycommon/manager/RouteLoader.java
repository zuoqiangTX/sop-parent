package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ServiceRouteInfo;

import java.util.function.Consumer;

/**
 * 路由定位器接口
 *
 * @author tanghc
 */
public interface RouteLoader {
    /**
     * 加载路由
     *
     * @param serviceRouteInfo 服务路由信息
     * @param callback         加载成功后回调
     */
    void load(ServiceRouteInfo serviceRouteInfo, Consumer<Object> callback);

    /**
     * 移除某个微服务下的所有路由信息
     *
     * @param serviceId 服务id
     */
    void remove(String serviceId);
}
