package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ServiceApiInfo;
import org.springframework.cloud.netflix.zuul.filters.Route;

/**
 * @author tanghc
 */
public interface ApiMetaContext {
    /**
     * 重新加载接口信息
     * @param serviceId
     * @param serviceApiInfo
     */
    void reload(String serviceId, ServiceApiInfo serviceApiInfo);

    /**
     * 获取接口对应的route信息
     * @param nameVersion
     * @return
     */
    Route getRoute(String nameVersion);
}
