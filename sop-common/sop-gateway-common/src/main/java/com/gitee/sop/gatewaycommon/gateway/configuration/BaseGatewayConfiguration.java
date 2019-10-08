package com.gitee.sop.gatewaycommon.gateway.configuration;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.gateway.filter.EnvGrayFilter;
import com.gitee.sop.gatewaycommon.gateway.filter.GatewayModifyResponseGatewayFilter;
import com.gitee.sop.gatewaycommon.gateway.filter.LimitFilter;
import com.gitee.sop.gatewaycommon.gateway.filter.LoadBalancerClientExtFilter;
import com.gitee.sop.gatewaycommon.gateway.filter.ParameterFormatterFilter;
import com.gitee.sop.gatewaycommon.gateway.filter.ValidateFilter;
import com.gitee.sop.gatewaycommon.gateway.handler.GatewayExceptionHandler;
import com.gitee.sop.gatewaycommon.gateway.route.GatewayRouteCache;
import com.gitee.sop.gatewaycommon.gateway.route.GatewayRouteRepository;
import com.gitee.sop.gatewaycommon.gateway.route.NameVersionRoutePredicateFactory;
import com.gitee.sop.gatewaycommon.gateway.route.ReadBodyRoutePredicateFactory;
import com.gitee.sop.gatewaycommon.manager.AbstractConfiguration;
import com.gitee.sop.gatewaycommon.manager.RouteRepositoryContext;
import com.gitee.sop.gatewaycommon.param.ParamBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collections;
import java.util.List;


/**
 * @author tanghc
 */
public class BaseGatewayConfiguration extends AbstractConfiguration {

    public BaseGatewayConfiguration() {
        ApiConfig.getInstance().setUseGateway(true);
    }

    /**
     * 自定义异常处理[@@]注册Bean时依赖的Bean，会从容器中直接获取，所以直接注入即可
     *
     * @param viewResolversProvider viewResolversProvider
     * @param serverCodecConfigurer serverCodecConfigurer
     */
    @Primary
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                                             ServerCodecConfigurer serverCodecConfigurer) {

        GatewayExceptionHandler jsonExceptionHandler = new GatewayExceptionHandler();
        jsonExceptionHandler.setViewResolvers(viewResolversProvider.getIfAvailable(Collections::emptyList));
        jsonExceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        jsonExceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return jsonExceptionHandler;
    }

    @Bean
    @ConditionalOnMissingBean
    ParamBuilder<ServerWebExchange> paramBuilder() {
        return ApiConfig.getInstance().getGatewayParamBuilder();
    }

    /**
     * 处理返回结果
     */
    @Bean
    GatewayModifyResponseGatewayFilter gatewayModifyResponseGatewayFilter() {
        return new GatewayModifyResponseGatewayFilter();
    }

    /**
     * 读取post请求参数
     */
    @Bean
    ReadBodyRoutePredicateFactory readBodyRoutePredicateFactory() {
        return new ReadBodyRoutePredicateFactory();
    }

    @Bean
    NameVersionRoutePredicateFactory paramRoutePredicateFactory() {
        return new NameVersionRoutePredicateFactory();
    }

    @Bean
    ValidateFilter validateFilter() {
        return new ValidateFilter();
    }

    @Bean
    ParameterFormatterFilter parameterFormatterFilter() {
        return new ParameterFormatterFilter();
    }

    @Bean
    LimitFilter limitFilter() {
        return new LimitFilter();
    }

    @Bean
    LoadBalancerClientExtFilter loadBalancerClientExtFilter() {
        return new LoadBalancerClientExtFilter();
    }

    @Bean
    GatewayRouteCache gatewayRouteCache(GatewayRouteRepository gatewayRouteRepository) {
        return new GatewayRouteCache(gatewayRouteRepository);
    }

    @Bean
    GatewayRouteRepository gatewayRouteRepository() {
        GatewayRouteRepository gatewayRouteRepository = new GatewayRouteRepository();
        RouteRepositoryContext.setRouteRepository(gatewayRouteRepository);
        return gatewayRouteRepository;
    }


    @Bean
    EnvGrayFilter envGrayFilter() {
        return new EnvGrayFilter();
    }
}
