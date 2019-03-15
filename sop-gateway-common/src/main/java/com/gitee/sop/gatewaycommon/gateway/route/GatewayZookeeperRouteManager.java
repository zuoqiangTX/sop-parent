package com.gitee.sop.gatewaycommon.gateway.route;

import com.gitee.sop.gatewaycommon.manager.BaseRouteManager;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 从zookeeper监听route信息
 *
 * @author tanghc
 */
@Getter
@Slf4j
public class GatewayZookeeperRouteManager extends BaseRouteManager<GatewayServiceRouteInfo, GatewayRouteDefinition, GatewayTargetRoute> {

    public GatewayZookeeperRouteManager(Environment environment, RouteRepository<GatewayTargetRoute> routeRepository) {
        super(environment, routeRepository);
    }

    @Override
    protected Class<GatewayServiceRouteInfo> getServiceRouteInfoClass() {
        return GatewayServiceRouteInfo.class;
    }

    @Override
    protected Class<GatewayRouteDefinition> getRouteDefinitionClass() {
        return GatewayRouteDefinition.class;
    }

    @Override
    protected GatewayTargetRoute buildRouteDefinition(GatewayServiceRouteInfo serviceRouteInfo, GatewayRouteDefinition gatewayRouteDefinition) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(gatewayRouteDefinition.getId());
        routeDefinition.setUri(URI.create(gatewayRouteDefinition.getUri()));
        routeDefinition.setOrder(gatewayRouteDefinition.getOrder());
        List<FilterDefinition> filterDefinitionList = new ArrayList<>(gatewayRouteDefinition.getFilters().size());
        List<PredicateDefinition> predicateDefinitionList = new ArrayList<>(gatewayRouteDefinition.getPredicates().size());
        for (GatewayFilterDefinition filter : gatewayRouteDefinition.getFilters()) {
            FilterDefinition filterDefinition = new FilterDefinition();
            BeanUtils.copyProperties(filter, filterDefinition);
            filterDefinitionList.add(filterDefinition);
        }

        for (GatewayPredicateDefinition predicate : gatewayRouteDefinition.getPredicates()) {
            PredicateDefinition predicateDefinition = new PredicateDefinition();
            BeanUtils.copyProperties(predicate, predicateDefinition);
            predicateDefinitionList.add(predicateDefinition);
        }

        routeDefinition.setFilters(filterDefinitionList);
        routeDefinition.setPredicates(predicateDefinitionList);
        return new GatewayTargetRoute(serviceRouteInfo, gatewayRouteDefinition, routeDefinition);
    }

}
