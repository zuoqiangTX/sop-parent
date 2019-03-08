package com.gitee.sop.gatewaycommon.bean;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author tanghc
 */
public abstract class ServiceRouteRepository<R, E> {
    /**
     * key:serviceId
     */
    private Map<String, Set<E>> serviceRouteMap = new ConcurrentHashMap<>();

    public abstract String getServiceId(R r);

    public void saveRouteDefinition(R serviceRouteInfo, E definition) {
        String serverId = getServiceId(serviceRouteInfo);
        Set<E> routeDefinitionSet = serviceRouteMap.putIfAbsent(serverId, new HashSet<>(16));
        if (routeDefinitionSet == null) {
            routeDefinitionSet = serviceRouteMap.get(serverId);
        }
        routeDefinitionSet.add(definition);
    }

    public synchronized void deleteAll(R serviceRouteInfo, Consumer<E> consumer) {
        String serverId = getServiceId(serviceRouteInfo);
        Set<E> definitionSet = serviceRouteMap.getOrDefault(serverId, Collections.emptySet());
        for (E routeDefinition : definitionSet) {
            consumer.accept(routeDefinition);
        }
        definitionSet.clear();
    }
}
