package com.gitee.sop.gatewaycommon.zuul.route;

import com.gitee.sop.gatewaycommon.manager.BaseRouteManager;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import com.gitee.sop.gatewaycommon.util.RoutePathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.core.env.Environment;

/**
 * @author tanghc
 */
@Slf4j
public class ZuulZookeeperRouteManager extends BaseRouteManager<ZuulServiceRouteInfo, ZuulRouteDefinition, Route> {

    public ZuulZookeeperRouteManager(Environment environment, RouteRepository<ZuulServiceRouteInfo, Route> routeRepository) {
        super(environment, routeRepository);
    }

    @Override
    protected Class<ZuulServiceRouteInfo> getServiceRouteInfoClass() {
        return ZuulServiceRouteInfo.class;
    }

    @Override
    protected Route buildRouteDefinition(ZuulServiceRouteInfo serviceRouteInfo, ZuulRouteDefinition routeDefinition) {
        return new Route(routeDefinition.getId(), RoutePathUtil.findPath(routeDefinition.getUri()), serviceRouteInfo.getAppName(), null, false, null);
    }
}
