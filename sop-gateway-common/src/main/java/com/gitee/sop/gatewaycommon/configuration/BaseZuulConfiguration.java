package com.gitee.sop.gatewaycommon.configuration;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.filter.ErrorFilter;
import com.gitee.sop.gatewaycommon.filter.PostResultFilter;
import com.gitee.sop.gatewaycommon.filter.PreValidateFilter;
import com.gitee.sop.gatewaycommon.manager.ApiMetaChangeListener;
import com.gitee.sop.gatewaycommon.manager.ApiMetaContext;
import com.gitee.sop.gatewaycommon.manager.ApiMetaManager;
import com.gitee.sop.gatewaycommon.manager.DefaultApiMetaContext;
import com.gitee.sop.gatewaycommon.manager.DefaultApiMetaManager;
import com.gitee.sop.gatewaycommon.manager.SopRouteLocator;
import com.gitee.sop.gatewaycommon.message.ErrorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.PostConstruct;

/**
 * @author tanghc
 */
public class BaseZuulConfiguration {

    public static final String API_CHANGE_CHANNEL = "channel.sop.api.change";


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
    PreValidateFilter preValidateFilter() {
        return new PreValidateFilter();
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
    ErrorFilter errorFilter() {
        return new ErrorFilter();
    }

    @Bean
    PostResultFilter postResultFilter() {
        return new PostResultFilter();
    }

    /**
     * 配置redis事件订阅
     *
     * @param apiMetaManager
     * @param redisTemplate
     * @return
     */
    @Bean
    RedisMessageListenerContainer container(ApiMetaManager apiMetaManager, StringRedisTemplate redisTemplate) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        ApiMetaChangeListener apiMetaChangeListener = new ApiMetaChangeListener(apiMetaManager, redisTemplate);
        container.addMessageListener(apiMetaChangeListener, new PatternTopic(API_CHANGE_CHANNEL));
        return container;
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
