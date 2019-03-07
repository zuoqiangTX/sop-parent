package com.gitee.sop.gatewaycommon.gateway.route;

import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * 动态更新路由
 * @author thc
 */
@Slf4j
public class GatewayRouteRepository implements ApplicationEventPublisherAware, RouteRepository<RouteDefinition> {

    @Autowired
    private RouteDefinitionRepository routeDefinitionRepository;

    private ApplicationEventPublisher publisher;

    /** 根据ID获取路由 */
    @Override
    public RouteDefinition get(String id) {
        return routeDefinitionRepository.getRouteDefinitions()
                .filter(routeDefinition -> {
                    return routeDefinition.getId().equals(id);
                }).blockFirst();
    }

    /** 增加路由 */
    @Override
    public String add(RouteDefinition definition) {
        routeDefinitionRepository.save(Mono.just(definition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return "success";
    }

    /** 更新路由 */
    @Override
    public String update(RouteDefinition definition) {
        log.info("更新route，id:{}", definition.getId());
        try {
            this.routeDefinitionRepository.delete(Mono.just(definition.getId()));
        } catch (Exception e) {
            return "update fail,not find route  routeId: " + definition.getId();
        }
        try {
            routeDefinitionRepository.save(Mono.just(definition)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception e) {
            return "update route  fail";
        }
    }

    /** 删除路由 */
    @Override
    public void delete(String id) {
        this.routeDefinitionRepository.delete(Mono.just(id))
                .then(Mono.defer(() -> Mono.just(ResponseEntity.ok().build())))
                .onErrorResume(t -> t instanceof NotFoundException, t -> Mono.just(ResponseEntity.notFound().build()));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}