package com.gitee.sop.gatewaycommon.gateway.route;

import com.gitee.sop.gatewaycommon.bean.RouteDefinition;
import com.gitee.sop.gatewaycommon.bean.ServiceRouteInfo;
import com.gitee.sop.gatewaycommon.manager.BaseRouteCache;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
public class GatewayRouteCache extends BaseRouteCache<GatewayTargetRoute> {

    public GatewayRouteCache(RouteRepository<GatewayTargetRoute> routeRepository) {
        super(routeRepository);
    }

    @Override
    protected GatewayTargetRoute buildTargetRoute(ServiceRouteInfo serviceRouteInfo, RouteDefinition routeDefinition) {
        org.springframework.cloud.gateway.route.RouteDefinition targetRoute = new org.springframework.cloud.gateway.route.RouteDefinition();
        targetRoute.setId(routeDefinition.getId());
        targetRoute.setUri(URI.create(routeDefinition.getUri() + "#" + routeDefinition.getPath()));
        targetRoute.setOrder(routeDefinition.getOrder());
        // 添加过滤器
        List<FilterDefinition> filterDefinitionList = routeDefinition.getFilters()
                .stream()
                .map(filter -> {
                    FilterDefinition filterDefinition = new FilterDefinition();
                    BeanUtils.copyProperties(filter, filterDefinition);
                    return filterDefinition;
                })
                .collect(Collectors.toList());


        LinkedList<PredicateDefinition> predicateDefinitionList = routeDefinition.getPredicates()
                .stream()
                .map(predicate -> {
                    PredicateDefinition predicateDefinition = new PredicateDefinition();
                    BeanUtils.copyProperties(predicate, predicateDefinition);
                    return predicateDefinition;
                })
                .collect(Collectors.toCollection(LinkedList::new));
        // 下面两个自定义的断言添加到顶部，注意：ReadBody需要放在最前面
        // 对应断言：
        // NameVersion ->   com.gitee.sop.gatewaycommon.gateway.route.NameVersionRoutePredicateFactory
        // ReadBody ->      com.gitee.sop.gatewaycommon.gateway.route.ReadBodyRoutePredicateFactory
        predicateDefinitionList.addFirst(new PredicateDefinition("NameVersion=" + routeDefinition.getId()));
        predicateDefinitionList.addFirst(new PredicateDefinition("ReadBody="));

        targetRoute.setFilters(filterDefinitionList);
        targetRoute.setPredicates(predicateDefinitionList);
        return new GatewayTargetRoute(serviceRouteInfo, routeDefinition, targetRoute);
    }

}
