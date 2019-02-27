package com.gitee.sop.gatewaycommon.manager;

/**
 * 管理各服务接口信息
 * @author tanghc
 */
public interface ApiMetaManager {

    String API_STORE_KEY = "com.gitee.sop.api";

    /**
     * 刷新素有的微服务接口信息
     */
    void refresh();

    /**
     * 某个服务接口更改时触发
     * @param serviceApiInfoJson 接口信息
     */
    void onChange(String serviceApiInfoJson);

}
