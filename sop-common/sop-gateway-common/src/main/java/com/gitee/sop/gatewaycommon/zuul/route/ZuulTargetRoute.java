package com.gitee.sop.gatewaycommon.zuul.route;

import com.gitee.sop.gatewaycommon.bean.AbstractTargetRoute;
import com.gitee.sop.gatewaycommon.bean.GatewayRouteDefinition;
import com.gitee.sop.gatewaycommon.bean.ServiceRouteInfo;
import lombok.Getter;
import org.springframework.cloud.netflix.zuul.filters.Route;

/**
 * @author tanghc
 */
@Getter
public class ZuulTargetRoute extends AbstractTargetRoute<Route> {

    public ZuulTargetRoute(ServiceRouteInfo serviceRouteInfo, GatewayRouteDefinition routeDefinition, Route targetRoute) {
        super(serviceRouteInfo, routeDefinition, targetRoute);
    }
}
