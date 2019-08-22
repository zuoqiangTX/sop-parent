package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.bean.BeanInitializer;
import com.gitee.sop.gatewaycommon.bean.SpringContext;
import com.gitee.sop.gatewaycommon.limit.LimitManager;
import com.gitee.sop.gatewaycommon.message.ErrorFactory;
import com.gitee.sop.gatewaycommon.param.ParameterFormatter;
import com.gitee.sop.gatewaycommon.secret.IsvManager;
import com.gitee.sop.gatewaycommon.session.SessionManager;
import com.gitee.sop.gatewaycommon.validate.Validator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;

/**
 * @author tanghc
 */
public class AbstractConfiguration implements ApplicationContextAware, ApplicationListener<HeartbeatEvent> {
    @Autowired
    protected Environment environment;

    @Autowired
    private ServiceRoutesLoader serviceRoutesLoader;

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    /**
     * nacos事件监听
     * @see org.springframework.cloud.alibaba.nacos.discovery.NacosWatch NacosWatch
     * @param heartbeatEvent
     */
    @Override
    public void onApplicationEvent(HeartbeatEvent heartbeatEvent) {
        serviceRoutesLoader.load(heartbeatEvent);
    }

    /**
     * 微服务路由加载
     */
    @Bean
    @ConditionalOnMissingBean
    ServiceRoutesLoader serviceRoutesLoader() {
        return new ServiceRoutesLoader();
    }

    @Bean
    @ConditionalOnMissingBean
    Validator validator() {
        return ApiConfig.getInstance().getValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    IsvManager isvManager() {
        return ApiConfig.getInstance().getIsvManager();
    }

    @Bean
    @ConditionalOnMissingBean
    IsvRoutePermissionManager isvRoutePermissionManager() {
        return ApiConfig.getInstance().getIsvRoutePermissionManager();
    }

    @Bean
    @ConditionalOnMissingBean
    RouteConfigManager routeConfigManager() {
        return ApiConfig.getInstance().getRouteConfigManager();
    }

    @Bean
    @ConditionalOnMissingBean
    LimitConfigManager limitConfigManager() {
        return ApiConfig.getInstance().getLimitConfigManager();
    }

    @Bean
    @ConditionalOnMissingBean
    LimitManager limitManager() {
        return ApiConfig.getInstance().getLimitManager();
    }

    @Bean
    @ConditionalOnMissingBean
    IPBlacklistManager ipBlacklistManager() {
        return ApiConfig.getInstance().getIpBlacklistManager();
    }

    @Bean
    @ConditionalOnMissingBean
    EnvGrayManager envGrayManager() {
        return ApiConfig.getInstance().getUserKeyManager();
    }

    @Bean
    @ConditionalOnMissingBean
    SessionManager sessionManager() {
        return ApiConfig.getInstance().getSessionManager();
    }

    @Bean
    @ConditionalOnMissingBean
    ParameterFormatter parameterFormatter() {
        return ApiConfig.getInstance().getParameterFormatter();
    }


    /**
     * 跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        return createCorsFilter();
    }

    protected CorsFilter createCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        this.registerCorsConfiguration(source);
        return new CorsFilter(source);
    }

    protected void registerCorsConfiguration(UrlBasedCorsConfigurationSource source) {
        source.registerCorsConfiguration("/**", corsConfiguration());
    }

    protected CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @PostConstruct
    public final void after() {
        SpringContext.setApplicationContext(applicationContext);
        if (RouteRepositoryContext.getRouteRepository() == null) {
            throw new IllegalArgumentException("RouteRepositoryContext.setRouteRepository()方法未使用");
        }
        EnvironmentContext.setEnvironment(environment);

        initMessage();
        initBeanInitializer();
        doAfter();

    }

    protected void initBeanInitializer() {
        String[] beanNames = applicationContext.getBeanNamesForType(BeanInitializer.class);
        if (beanNames != null) {
            for (String beanName : beanNames) {
                BeanInitializer beanInitializer = applicationContext.getBean(beanName, BeanInitializer.class);
                beanInitializer.load();
            }
        }
    }

    protected void doAfter() {

    }

    protected void initMessage() {
        ErrorFactory.initMessageSource(ApiContext.getApiConfig().getI18nModules());
    }
}
