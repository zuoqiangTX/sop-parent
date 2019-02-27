package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.core.Ordered;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author tanghc
 */
public class SopRouteLocator implements RouteLocator, Ordered {

    private ApiMetaContext apiMetaContext;

    public SopRouteLocator(ApiMetaContext apiMetaContext) {
        this.apiMetaContext = apiMetaContext;
    }

    @Override
    public Collection<String> getIgnoredPaths() {
        return Collections.emptyList();
    }

    @Override
    public List<Route> getRoutes() {
        return null;
    }

    @Override
    public Route getMatchingRoute(String path) {
        ApiParam param = ApiContext.getApiParam();
        String nameVersion = param.fetchNameVersion();
        return apiMetaContext.getRoute(nameVersion);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
