package com.gitee.sop.gatewaycommon.zuul.configuration;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.manager.AbstractConfiguration;
import com.gitee.sop.gatewaycommon.manager.RouteRepositoryContext;
import com.gitee.sop.gatewaycommon.zuul.filter.ErrorFilter;
import com.gitee.sop.gatewaycommon.zuul.filter.PostResultFilter;
import com.gitee.sop.gatewaycommon.zuul.filter.PreRoutePermissionFilter;
import com.gitee.sop.gatewaycommon.zuul.filter.PreValidateFilter;
import com.gitee.sop.gatewaycommon.zuul.route.SopRouteLocator;
import com.gitee.sop.gatewaycommon.zuul.route.ZuulRouteRepository;
import com.gitee.sop.gatewaycommon.zuul.route.ZuulZookeeperRouteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author tanghc
 */
public class BaseZuulConfiguration extends AbstractConfiguration {

    @Autowired
    protected ZuulProperties zuulProperties;

    @Autowired
    protected ServerProperties server;

    /**
     * 路由存储
     * @return
     */
    @Bean
    ZuulRouteRepository zuulRouteRepository() {
        ZuulRouteRepository zuulRouteRepository = new ZuulRouteRepository();
        RouteRepositoryContext.setRouteRepository(zuulRouteRepository);
        return zuulRouteRepository;
    }

    /**
     * 选取路由
     * @param zuulRouteRepository
     * @param proxyRequestHelper
     * @return
     */
    @Bean
    public PreDecorationFilter preDecorationFilter(ZuulRouteRepository zuulRouteRepository, ProxyRequestHelper proxyRequestHelper) {
        // 自定义路由
        RouteLocator routeLocator = new SopRouteLocator(zuulRouteRepository);
        return new PreDecorationFilter(routeLocator,
                this.server.getServlet().getContextPath(),
                this.zuulProperties,
                proxyRequestHelper);
    }

    /**
     * 路由管理
     * @param environment
     * @param zuulRouteRepository
     * @return
     */
    @Bean
    ZuulZookeeperRouteManager zuulZookeeperRouteManager(Environment environment, ZuulRouteRepository zuulRouteRepository) {
        return new ZuulZookeeperRouteManager(environment, zuulRouteRepository);
    }

    /**
     * 前置校验
     * @return
     */
    @Bean
    PreValidateFilter preValidateFilter() {
        return new PreValidateFilter();
    }

    /**
     * 权限校验
     * @return
     */
    @Bean
    PreRoutePermissionFilter preRoutePermissionFilter() {
        return new PreRoutePermissionFilter();
    }

    /**
     * 错误处理扩展
     * @return
     */
    @Bean
    ErrorFilter errorFilter() {
        return new ErrorFilter();
    }

    /**
     * 结果返回
     * @return
     */
    @Bean
    PostResultFilter postResultFilter() {
        return new PostResultFilter();
    }

    /**
     * 统一错误处理
     * @return
     */
    @Bean
    ZuulErrorController baseZuulController() {
        return ApiContext.getApiConfig().getZuulErrorController();
    }

}
