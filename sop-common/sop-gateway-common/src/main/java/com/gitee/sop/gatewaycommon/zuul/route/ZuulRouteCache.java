package com.gitee.sop.gatewaycommon.zuul.route;

import com.gitee.sop.gatewaycommon.bean.GatewayRouteDefinition;
import com.gitee.sop.gatewaycommon.bean.ServiceRouteInfo;
import com.gitee.sop.gatewaycommon.manager.BaseRouteCache;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import com.gitee.sop.gatewaycommon.util.RouteUtil;
import org.springframework.cloud.netflix.zuul.filters.Route;

/**
 * @author tanghc
 */
public class ZuulRouteCache extends BaseRouteCache<ZuulTargetRoute> {

    /** 路由重试 */
    public static final boolean RETRYABLE = true;

    public ZuulRouteCache(RouteRepository<ZuulTargetRoute> routeRepository) {
        super(routeRepository);
    }

    @Override
    protected ZuulTargetRoute buildRouteDefinition(ServiceRouteInfo serviceRouteInfo, GatewayRouteDefinition gatewayRouteDefinition) {
        Route route = new Route(
                gatewayRouteDefinition.getId()
                , gatewayRouteDefinition.getPath()
                , RouteUtil.getZuulLocation(gatewayRouteDefinition.getUri())
                , ""
                , RETRYABLE
                , null
        );
        return new ZuulTargetRoute(serviceRouteInfo, gatewayRouteDefinition, route);
    }
}
