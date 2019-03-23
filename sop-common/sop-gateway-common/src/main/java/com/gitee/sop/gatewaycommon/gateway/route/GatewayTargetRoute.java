package com.gitee.sop.gatewaycommon.gateway.route;

import com.gitee.sop.gatewaycommon.bean.AbstractTargetRoute;
import org.springframework.cloud.gateway.route.RouteDefinition;

/**
 * @author tanghc
 */
public class GatewayTargetRoute extends AbstractTargetRoute<GatewayServiceRouteInfo, GatewayRouteDefinition, RouteDefinition> {
    public GatewayTargetRoute(GatewayServiceRouteInfo baseServiceRouteInfo, GatewayRouteDefinition baseRouteDefinition, RouteDefinition targetRoute) {
        super(baseServiceRouteInfo, baseRouteDefinition, targetRoute);
    }
}
