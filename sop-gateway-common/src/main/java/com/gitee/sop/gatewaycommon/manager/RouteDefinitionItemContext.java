package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.BaseRouteDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanghc
 */
public class RouteDefinitionItemContext {
    // key:id
    private static Map<String, BaseRouteDefinition> routeDefinitionMap = new HashMap<>(64);

    public static void add(BaseRouteDefinition routeDefinition) {
        routeDefinitionMap.put(routeDefinition.getId(), routeDefinition);
    }

    public static BaseRouteDefinition getRouteDefinition(String id) {
        return routeDefinitionMap.get(id);
    }

    public static void delete(BaseRouteDefinition routeDefinition) {
        routeDefinitionMap.remove(routeDefinition.getId());
    }

}
