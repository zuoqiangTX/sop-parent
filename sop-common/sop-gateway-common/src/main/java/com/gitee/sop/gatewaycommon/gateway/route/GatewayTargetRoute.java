package com.gitee.sop.gatewaycommon.gateway.route;

import com.gitee.sop.gatewaycommon.bean.AbstractTargetRoute;
import com.gitee.sop.gatewaycommon.bean.GatewayRouteDefinition;
import com.gitee.sop.gatewaycommon.bean.ServiceRouteInfo;
import org.springframework.cloud.gateway.route.RouteDefinition;

/**
 * @author tanghc
 */
public class GatewayTargetRoute extends AbstractTargetRoute<RouteDefinition> {

    public GatewayTargetRoute(ServiceRouteInfo serviceRouteInfo, GatewayRouteDefinition routeDefinition, RouteDefinition targetRoute) {
        super(serviceRouteInfo, routeDefinition, targetRoute);
    }
}
