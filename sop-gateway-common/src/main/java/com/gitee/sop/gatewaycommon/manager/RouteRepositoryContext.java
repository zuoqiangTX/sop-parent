package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.TargetRoute;

/**
 * @author tanghc
 */
public class RouteRepositoryContext {
    private static RouteRepository<? extends TargetRoute> routeRepository;

    public static RouteRepository<? extends TargetRoute> getRouteRepository() {
        return routeRepository;
    }

    public static <T extends TargetRoute> void  setRouteRepository(RouteRepository<T> routeRepository) {
        RouteRepositoryContext.routeRepository = routeRepository;
    }

}
