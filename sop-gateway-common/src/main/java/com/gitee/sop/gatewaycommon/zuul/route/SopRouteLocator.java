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

    private RouteRepository<Route> routeRepository;

    public SopRouteLocator(RouteRepository<Route> routeRepository) {
        this.routeRepository = routeRepository;
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
        ApiParam param = ZuulContext.getApiParam();
        String nameVersion = param.fetchNameVersion();
        return routeRepository.get(nameVersion);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
