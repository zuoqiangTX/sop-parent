package com.gitee.sop.gatewaycommon.zuul.route;

import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.zuul.ZuulContext;
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

    private ZuulRouteRepository zuulRouteRepository;

    public SopRouteLocator(ZuulRouteRepository zuulRouteRepository) {
        this.zuulRouteRepository = zuulRouteRepository;
    }

    @Override
    public Collection<String> getIgnoredPaths() {
        return Collections.emptyList();
    }

    @Override
    public List<Route> getRoutes() {
        return zuulRouteRepository.listAll();
    }

    @Override
    public Route getMatchingRoute(String path) {
        ApiParam param = ZuulContext.getApiParam();
        String nameVersion = param.fetchNameVersion();
        return zuulRouteRepository.get(nameVersion);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
