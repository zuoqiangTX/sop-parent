package com.gitee.sop.gatewaycommon.gateway.route;

import com.gitee.sop.gatewaycommon.bean.ServiceRouteRepository;
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
import org.springframework.cloud.gateway.support.ConfigurationUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由存储管理，负责动态更新路由
 *
 * @author thc
 */
@Slf4j
public class GatewayRouteRepository extends InMemoryRouteDefinitionRepository
        implements ApplicationEventPublisherAware,
        BeanFactoryAware,
        RouteRepository<GatewayServiceRouteInfo, RouteDefinition> {

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private ServiceRouteRepository<GatewayServiceRouteInfo, RouteDefinition> serviceRouteRepository = new ServiceRouteRepository<GatewayServiceRouteInfo, RouteDefinition>() {
        @Override
        public String getServiceId(GatewayServiceRouteInfo serviceRouteInfo) {
            return serviceRouteInfo.getAppName();
        }
    };

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private Validator validator;

    private ApplicationEventPublisher publisher;

    private BeanFactory beanFactory;

    /**
     * 根据ID获取路由
     */
    @Override
    public RouteDefinition get(String id) {
        return getRouteDefinitions()
                .filter(routeDefinition -> {
                    return routeDefinition.getId().equals(id);
                }).blockFirst();
    }

    /**
     * 增加路由
     */
    @Override
    public String add(GatewayServiceRouteInfo serviceRouteInfo, RouteDefinition definition) {
        super.save(Mono.just(definition)).subscribe();
        serviceRouteRepository.saveRouteDefinition(serviceRouteInfo, definition);
        this.initPredicateDefinition(definition);
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return "success";
    }

    protected void initPredicateDefinition(RouteDefinition definition) {
        for (PredicateDefinition predicate : definition.getPredicates()) {
            Map<String, String> args = predicate.getArgs();
            if (!args.isEmpty()) {
                RoutePredicateFactory<NameVersionRoutePredicateFactory.Config> factory = new NameVersionRoutePredicateFactory();
                Map<String, Object> properties = factory.shortcutType().normalize(args, factory, this.parser, this.beanFactory);
                Object config = factory.newConfig();
                ConfigurationUtils.bind(config, properties, factory.shortcutFieldPrefix(), predicate.getName(),
                        validator, conversionService);
                this.publisher.publishEvent(new PredicateArgsEvent(this, definition.getId(), properties));
            }
        }

    }

    /**
     * 删除路由
     */
    @Override
    public void delete(String id) {
        super.delete(Mono.just(id));
        this.publisher.publishEvent(new PredicateArgsEvent(this, id, Collections.emptyMap()));
    }

    @Override
    public void deleteAll(GatewayServiceRouteInfo serviceRouteInfo) {
        serviceRouteRepository.deleteAll(serviceRouteInfo, routeDefinition -> {
            this.delete(routeDefinition.getId());
        });
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