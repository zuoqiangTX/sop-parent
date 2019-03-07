package com.gitee.sop.gatewaycommon.zuul.route;

import com.gitee.sop.gatewaycommon.manager.BaseRouteManager;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.core.env.Environment;

/**
 * @author tanghc
 */
@Slf4j
public class ZuulZookeeperRouteManager extends BaseRouteManager<ZuulRouteDefinition, ZuulServiceRouteInfo, Route> {

    public ZuulZookeeperRouteManager(Environment environment, RouteRepository<Route> routeRepository) {
        super(environment, routeRepository);
    }

    @Override
    protected Class<ZuulServiceRouteInfo> getServiceRouteInfoClass() {
        return ZuulServiceRouteInfo.class;
    }

    @Override
    protected Class<ZuulRouteDefinition> getRouteDefinitionClass() {
        return ZuulRouteDefinition.class;
    }

    @Override
    protected Route buildRouteDefinition(ZuulServiceRouteInfo serviceRouteInfo, ZuulRouteDefinition routeDefinition) {
        return new Route(routeDefinition.getId(), routeDefinition.getPath(), serviceRouteInfo.getAppName(), null, false, null);
    }
}
