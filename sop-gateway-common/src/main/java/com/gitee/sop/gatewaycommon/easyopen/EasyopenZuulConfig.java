package com.gitee.sop.gatewaycommon.easyopen;

import com.gitee.sop.gatewaycommon.filter.ErrorFilter;
import com.gitee.sop.gatewaycommon.filter.PostResultFilter;
import com.gitee.sop.gatewaycommon.easyopen.filter.PostEasyopenResultFilter;
import com.gitee.sop.gatewaycommon.filter.PreValidateFilter;
import com.gitee.sop.gatewaycommon.manager.ApiMetaContext;
import com.gitee.sop.gatewaycommon.manager.ApiMetaManager;
import com.gitee.sop.gatewaycommon.manager.DefaultApiMetaContext;
import com.gitee.sop.gatewaycommon.manager.DefaultApiMetaManager;
import com.gitee.sop.gatewaycommon.manager.SopRouteLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;

/**
 * @author tanghc
 */
public class EasyopenZuulConfig {

    @Autowired
    protected ZuulProperties zuulProperties;

    @Autowired
    protected ServerProperties server;

    @Autowired
    protected ApiMetaManager apiMetaManager;

    @Bean
    public ApiMetaContext apiMetaContext() {
        return new DefaultApiMetaContext();
    }

    @Bean
    public ApiMetaManager apiMetaManager(StringRedisTemplate stringRedisTemplate, ApiMetaContext apiMetaContext) {
        return new DefaultApiMetaManager(stringRedisTemplate, apiMetaContext);
    }

    @Bean
    public PreDecorationFilter preDecorationFilter(ApiMetaContext apiMetaContext, ProxyRequestHelper proxyRequestHelper) {
        SopRouteLocator routeLocator = new SopRouteLocator(apiMetaContext);
        return new PreDecorationFilter(routeLocator,
                this.server.getServlet().getContextPath(),
                this.zuulProperties,
                proxyRequestHelper);
    }

    @Bean
    PreValidateFilter preValidateFilter() {
        return new PreValidateFilter();
    }

    @Bean
    ErrorFilter errorFilter() {
        return new ErrorFilter();
    }

    @Bean
    PostResultFilter postResultFilter() {
        return new PostEasyopenResultFilter();
    }

    @PostConstruct
    public void after() {
        apiMetaManager.refresh();
    }
}
