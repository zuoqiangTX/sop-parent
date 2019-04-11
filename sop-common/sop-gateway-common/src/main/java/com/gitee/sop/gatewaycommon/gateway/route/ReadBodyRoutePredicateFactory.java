package com.gitee.sop.gatewaycommon.gateway.route;

import org.springframework.cloud.gateway.handler.predicate.ReadBodyPredicateFactory;

import java.util.Objects;

/**
 * 获取form表单插件，使用方式：
 *     &#64;Bean
 *     ReadBodyRoutePredicateFactory readBodyRoutePredicateFactory() {
 *         return new ReadBodyRoutePredicateFactory();
 *     }
 *
 * @author tanghc
 */
public class ReadBodyRoutePredicateFactory extends ReadBodyPredicateFactory {

    @Override
    public Config newConfig() {
        Config config = super.newConfig();
        config.setInClass(String.class);
        config.setPredicate(Objects::nonNull);
        return config;
    }
}
