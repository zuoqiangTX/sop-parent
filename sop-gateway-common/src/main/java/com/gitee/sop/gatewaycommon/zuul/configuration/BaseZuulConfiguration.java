package com.gitee.sop.gatewaycommon.zuul.configuration;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.manager.RouteManager;
import com.gitee.sop.gatewaycommon.message.ErrorFactory;
import com.gitee.sop.gatewaycommon.zuul.filter.ErrorFilter;
import com.gitee.sop.gatewaycommon.zuul.filter.PostResultFilter;
import com.gitee.sop.gatewaycommon.zuul.filter.PreValidateFilter;
import com.gitee.sop.gatewaycommon.zuul.route.SopRouteLocator;
import com.gitee.sop.gatewaycommon.zuul.route.ZuulRouteRepository;
import com.gitee.sop.gatewaycommon.zuul.route.ZuulZookeeperRouteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * @author tanghc
 */
public class BaseZuulConfiguration {

    @Autowired
    protected ZuulProperties zuulProperties;

    @Autowired
    protected ServerProperties server;

    @Autowired
    protected Environment environment;

    @Autowired
    protected RouteManager apiMetaManager;

    @Bean
    ZuulZookeeperRouteManager zuulZookeeperRouteManager(Environment environment, ZuulRouteRepository zuulRouteRepository) {
        return new ZuulZookeeperRouteManager(environment, zuulRouteRepository);
    }

    @Bean
    ZuulRouteRepository zuulRouteRepository() {
        return new ZuulRouteRepository();
    }

    @Bean
    PreValidateFilter preValidateFilter() {
        return new PreValidateFilter();
    }

    @Bean
    public PreDecorationFilter preDecorationFilter(ZuulRouteRepository zuulRouteRepository, ProxyRequestHelper proxyRequestHelper) {
        SopRouteLocator routeLocator = new SopRouteLocator(zuulRouteRepository);
        return new PreDecorationFilter(routeLocator,
                this.server.getServlet().getContextPath(),
                this.zuulProperties,
                proxyRequestHelper);
    }

    @Bean
    ErrorFilter errorFilter() {
        return new ErrorFilter();
    }

    @Bean
    PostResultFilter postResultFilter() {
        return new PostResultFilter();
    }

    @Bean
    BaseZuulController baseZuulController() {
        return ApiContext.getApiConfig().getBaseZuulController();
    }

    @PostConstruct
    public void after() {
        doAfter();
    }

    protected void doAfter() {
        initMessage();
        apiMetaManager.refresh();
    }

    protected void initMessage() {
        ErrorFactory.initMessageSource(ApiContext.getApiConfig().getI18nModules());
    }

}
