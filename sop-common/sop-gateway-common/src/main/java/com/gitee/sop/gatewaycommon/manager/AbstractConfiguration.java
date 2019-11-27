package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.bean.BeanInitializer;
import com.gitee.sop.gatewaycommon.bean.SpringContext;
import com.gitee.sop.gatewaycommon.limit.LimitManager;
import com.gitee.sop.gatewaycommon.loadbalancer.SopPropertiesFactory;
import com.gitee.sop.gatewaycommon.message.ErrorFactory;
import com.gitee.sop.gatewaycommon.param.ParameterFormatter;
import com.gitee.sop.gatewaycommon.route.ServiceRouteListener;
import com.gitee.sop.gatewaycommon.route.EurekaRegistryListener;
import com.gitee.sop.gatewaycommon.route.NacosRegistryListener;
import com.gitee.sop.gatewaycommon.route.RegistryListener;
import com.gitee.sop.gatewaycommon.route.ServiceListener;
import com.gitee.sop.gatewaycommon.secret.IsvManager;
import com.gitee.sop.gatewaycommon.session.SessionManager;
import com.gitee.sop.gatewaycommon.validate.SignConfig;
import com.gitee.sop.gatewaycommon.validate.Validator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;

/**
 * ApplicationContextAware 获取ApplicationContext 上下文
 *
 * @author tanghc
 */
public class AbstractConfiguration implements ApplicationContextAware {

    //   环境属性
    @Autowired
    protected Environment environment;

    //    注册中心监听器
    @Autowired
    private RegistryListener registryListener;

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    /**
     * nacos事件监听
     *
     * @param heartbeatEvent
     */
    @EventListener(classes = HeartbeatEvent.class)
    public void listenNacosEvent(ApplicationEvent heartbeatEvent) {
        registryListener.onEvent(heartbeatEvent);
    }

    @Bean
    @ConditionalOnProperty("zuul.servlet-path")
    PropertiesFactory propertiesFactory() {
        return new SopPropertiesFactory();
    }

    /**
     * 微服务路由加载
     */
    @Bean
    @ConditionalOnProperty("spring.cloud.nacos.discovery.server-addr")
    RegistryListener registryListenerNacos() {
        return new NacosRegistryListener();
    }

    @Bean
    @ConditionalOnProperty("eureka.client.serviceUrl.defaultZone")
    RegistryListener registryListenerEureka() {
        return new EurekaRegistryListener();
    }

    @Bean
    @ConditionalOnMissingBean
    ServiceListener serviceListener() {
        return new ServiceRouteListener();
    }

    //    接口校验器
    @Bean
    @ConditionalOnMissingBean
    Validator validator() {
        return ApiConfig.getInstance().getValidator();
    }

    //    isv管理类
    @Bean
    @ConditionalOnMissingBean
    IsvManager isvManager() {
        return ApiConfig.getInstance().getIsvManager();
    }

    //    路由权限管理类
    @Bean
    @ConditionalOnMissingBean
    IsvRoutePermissionManager isvRoutePermissionManager() {
        return ApiConfig.getInstance().getIsvRoutePermissionManager();
    }

    //    路由配置管理
    @Bean
    @ConditionalOnMissingBean
    RouteConfigManager routeConfigManager() {
        return ApiConfig.getInstance().getRouteConfigManager();
    }

    //    限流配置
    @Bean
    @ConditionalOnMissingBean
    LimitConfigManager limitConfigManager() {
        return ApiConfig.getInstance().getLimitConfigManager();
    }

    //    限流管理
    @Bean
    @ConditionalOnMissingBean
    LimitManager limitManager() {
        return ApiConfig.getInstance().getLimitManager();
    }

    //    黑名单管理
    @Bean
    @ConditionalOnMissingBean
    IPBlacklistManager ipBlacklistManager() {
        return ApiConfig.getInstance().getIpBlacklistManager();
    }

    //    灰度管理
    @Bean
    @ConditionalOnMissingBean
    EnvGrayManager envGrayManager() {
        return ApiConfig.getInstance().getUserKeyManager();
    }

    //    会话管理
    @Bean
    @ConditionalOnMissingBean
    SessionManager sessionManager() {
        return ApiConfig.getInstance().getSessionManager();
    }

    //    参数格式化管理
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

    //    完成依赖项注入以执行
    @PostConstruct
    public final void after() {
        EnvironmentContext.setEnvironment(environment);
        SpringContext.setApplicationContext(applicationContext);
        if (RouteRepositoryContext.getRouteRepository() == null) {
            throw new IllegalArgumentException("RouteRepositoryContext.setRouteRepository()方法未使用");
        }
        String serverName = EnvironmentKeys.SPRING_APPLICATION_NAME.getValue();
        if (!"sop-gateway".equals(serverName)) {
            throw new IllegalArgumentException("spring.application.name必须为sop-gateway");
        }
        String urlencode = EnvironmentKeys.SIGN_URLENCODE.getValue();
        if ("true".equals(urlencode)) {
            SignConfig.enableUrlencodeMode();
        }

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
