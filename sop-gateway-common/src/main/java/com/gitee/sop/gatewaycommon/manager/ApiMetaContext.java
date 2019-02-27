package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ServiceApiInfo;
import org.springframework.cloud.netflix.zuul.filters.Route;

/**
 * @author tanghc
 */
public interface ApiMetaContext {
    void reload(String serviceId, ServiceApiInfo serviceApiInfo);

    Route getRoute(String nameVersion);
}
