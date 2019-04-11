package com.gitee.sop.gatewaycommon.gateway.route;

import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.PredicateArgsEvent;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.ConfigurationUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.Validator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.synchronizedMap;

/**
 * 路由存储管理，负责动态更新路由
 *
 * @author thc
 */
@Slf4j
public class GatewayRouteRepository implements ApplicationEventPublisherAware,
        RouteDefinitionRepository,
        BeanFactoryAware,
        RouteRepository<GatewayTargetRoute> {

    private final Map<String, GatewayTargetRoute> routes = synchronizedMap(new LinkedHashMap<>());

    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private Validator validator;

    private ApplicationEventPublisher publisher;

    private BeanFactory beanFactory;

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> list = routes.values().parallelStream()
                .map(TargetRoute::getTargetRouteDefinition)
                .collect(Collectors.toList());
        return Flux.fromIterable(list);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }

    /**
     * 根据ID获取路由
     */
    @Override
    public GatewayTargetRoute get(String id) {
        return routes.get(id);
    }

    @Override
    public Collection<GatewayTargetRoute> getAll() {
        return routes.values();
    }

    /**
     * 增加路由
     */
    @Override
    public String add(GatewayTargetRoute targetRoute) {
        GatewayRouteDefinition baseRouteDefinition = targetRoute.getRouteDefinition();
        routes.put(baseRouteDefinition.getId(), targetRoute);
        this.initPredicateDefinition(targetRoute);
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return "success";
    }

    @Override
    public void update(GatewayTargetRoute targetRoute) {
        GatewayRouteDefinition baseRouteDefinition = targetRoute.getRouteDefinition();
        routes.put(baseRouteDefinition.getId(), targetRoute);
    }

    protected void initPredicateDefinition(GatewayTargetRoute targetRoute) {
        GatewayRouteDefinition routeDefinition = targetRoute.getRouteDefinition();
        RouteDefinition targetRouteDefinition = targetRoute.getTargetRouteDefinition();
        for (PredicateDefinition predicate : targetRouteDefinition.getPredicates()) {
            Map<String, String> args = predicate.getArgs();
            if (!args.isEmpty()) {
                RoutePredicateFactory<NameVersionRoutePredicateFactory.Config> factory = new NameVersionRoutePredicateFactory();
                Map<String, Object> properties = factory.shortcutType().normalize(args, factory, this.parser, this.beanFactory);
                Object config = factory.newConfig();
                ConfigurationUtils.bind(config, properties, factory.shortcutFieldPrefix(), predicate.getName(),
                        validator, conversionService);
                this.publisher.publishEvent(new PredicateArgsEvent(this, routeDefinition.getId(), properties));
            }
        }

    }

    /**
     * 删除路由
     */
    @Override
    public void delete(String id) {
        routes.remove(id);
        this.publisher.publishEvent(new PredicateArgsEvent(this, id, Collections.emptyMap()));
    }

    @Override
    public void deleteAll(String serviceId) {
        List<String> idList = this.routes.values().stream()
                .map(zuulTargetRoute -> zuulTargetRoute.getRouteDefinition().getId())
                .collect(Collectors.toList());

        for (String id : idList) {
            this.delete(id);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}