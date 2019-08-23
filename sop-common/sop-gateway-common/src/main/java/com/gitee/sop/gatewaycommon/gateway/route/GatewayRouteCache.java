package com.gitee.sop.gatewaycommon.gateway.route;

import com.gitee.sop.gatewaycommon.bean.GatewayFilterDefinition;
import com.gitee.sop.gatewaycommon.bean.GatewayPredicateDefinition;
import com.gitee.sop.gatewaycommon.bean.RouteDefinition;
import com.gitee.sop.gatewaycommon.bean.ServiceRouteInfo;
import com.gitee.sop.gatewaycommon.manager.BaseRouteCache;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author tanghc
 */
public class GatewayRouteCache extends BaseRouteCache<GatewayTargetRoute> {

    public GatewayRouteCache(RouteRepository<GatewayTargetRoute> routeRepository) {
        super(routeRepository);
    }

    @Override
    protected GatewayTargetRoute buildRouteDefinition(ServiceRouteInfo serviceRouteInfo, RouteDefinition gatewayRouteDefinition) {
        org.springframework.cloud.gateway.route.RouteDefinition routeDefinition = new org.springframework.cloud.gateway.route.RouteDefinition();
        routeDefinition.setId(gatewayRouteDefinition.getId());
        routeDefinition.setUri(URI.create(gatewayRouteDefinition.getUri() + "#" + gatewayRouteDefinition.getPath()));
        routeDefinition.setOrder(gatewayRouteDefinition.getOrder());
        List<FilterDefinition> filterDefinitionList = new ArrayList<>(gatewayRouteDefinition.getFilters().size());
        LinkedList<PredicateDefinition> predicateDefinitionList = new LinkedList<>();
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
        this.addPredicate(predicateDefinitionList, "NameVersion", gatewayRouteDefinition.getId());
        this.addPredicate(predicateDefinitionList, "ReadBody", "");
        routeDefinition.setFilters(filterDefinitionList);
        routeDefinition.setPredicates(predicateDefinitionList);
        return new GatewayTargetRoute(serviceRouteInfo, gatewayRouteDefinition, routeDefinition);
    }

    /**
     * 添加断言
     *
     * @param predicateDefinitionList
     * @param name                    断言名称
     * @param args                    断言参数
     */
    protected void addPredicate(LinkedList<PredicateDefinition> predicateDefinitionList, String name, String args) {
        for (PredicateDefinition predicateDefinition : predicateDefinitionList) {
            // 如果已经存在，直接返回
            if (predicateDefinition.getName().equals(name)) {
                return;
            }
        }
        predicateDefinitionList.addFirst(new PredicateDefinition(name + "=" + args));
    }
}
