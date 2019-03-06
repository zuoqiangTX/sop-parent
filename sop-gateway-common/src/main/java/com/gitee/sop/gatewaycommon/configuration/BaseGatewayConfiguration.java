package com.gitee.sop.gatewaycommon.configuration;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.handler.GatewayExceptionHandler;
import com.gitee.sop.gatewaycommon.filter.GatewayModifyResponseGatewayFilter;
import com.gitee.sop.gatewaycommon.filter.LoadBalancerClientExtFilter;
import com.gitee.sop.gatewaycommon.filter.ValidateFilter;
import com.gitee.sop.gatewaycommon.manager.GatewayZookeeperApiMetaManager;
import com.gitee.sop.gatewaycommon.message.ErrorFactory;
import com.gitee.sop.gatewaycommon.route.DynamicRouteServiceManager;
import com.gitee.sop.gatewaycommon.route.NameVersionRoutePredicateFactory;
import com.gitee.sop.gatewaycommon.route.ReadBodyRoutePredicateFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

//import com.gitee.sop.gatewaycommon.filter.ErrorFilter;
//import com.gitee.sop.gatewaycommon.filter.PostResultFilter;
//import com.gitee.sop.gatewaycommon.filter.PreValidateFilter;
//import com.gitee.sop.gatewaycommon.manager.DefaultApiMetaContext;
//import com.gitee.sop.gatewaycommon.manager.SopRouteLocator;
//import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
//import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
//import org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter;

/**
 * @author tanghc
 */
public class BaseGatewayConfiguration {

    @Autowired
    protected Environment environment;

    @Autowired
    protected GatewayZookeeperApiMetaManager gatewayZookeeperApiMetaManager;

    /**
     * 自定义异常处理[@@]注册Bean时依赖的Bean，会从容器中直接获取，所以直接注入即可
     *
     * @param viewResolversProvider
     * @param serverCodecConfigurer
     * @return
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
    GatewayModifyResponseGatewayFilter gatewayModifyResponseGatewayFilter() {
        return new GatewayModifyResponseGatewayFilter();
    }

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
    LoadBalancerClientExtFilter loadBalancerClientExtFilter() {
        return new LoadBalancerClientExtFilter();
    }

    @Bean
    GatewayZookeeperApiMetaManager zookeeperApiMetaManager(Environment environment, DynamicRouteServiceManager dynamicRouteServiceManager) {
        return new GatewayZookeeperApiMetaManager(environment, dynamicRouteServiceManager);
    }

    @Bean
    DynamicRouteServiceManager dynamicRouteServiceManager() {
        return new DynamicRouteServiceManager();
    }


    @PostConstruct
    public void after() {
        doAfter();
    }

    protected void doAfter() {
        initMessage();
        gatewayZookeeperApiMetaManager.refresh();
    }

    protected void initMessage() {
        ErrorFactory.initMessageSource(ApiContext.getApiConfig().getI18nModules());
    }

}
