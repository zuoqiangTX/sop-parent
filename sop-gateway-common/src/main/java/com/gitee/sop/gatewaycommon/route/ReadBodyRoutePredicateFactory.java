package com.gitee.sop.gatewaycommon.route;

import org.springframework.cloud.gateway.handler.predicate.ReadBodyPredicateFactory;

/**
 * 获取form表单插件，使用方式：
 *      @Bean
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
        config.setPredicate(body -> {
            return body != null;
        });
        return config;
    }
}
