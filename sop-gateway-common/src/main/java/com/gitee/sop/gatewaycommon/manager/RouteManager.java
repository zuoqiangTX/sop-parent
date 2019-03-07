package com.gitee.sop.gatewaycommon.manager;

/**
 * 管理各服务路由信息
 * @author tanghc
 */
public interface RouteManager {

    String API_STORE_KEY = "com.gitee.sop.api";

    /**
     * 刷新素有的微服务接口信息
     */
    void refresh();

}
