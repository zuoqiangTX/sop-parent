package com.gitee.sop.servercommon.configuration;

import com.gitee.sop.servercommon.bean.ServiceConfig;
import com.gitee.sop.servercommon.interceptor.ServiceContextInterceptor;
import com.gitee.sop.servercommon.manager.ApiMetaManager;
import com.gitee.sop.servercommon.manager.DefaultRequestMappingEvent;
import com.gitee.sop.servercommon.manager.RequestMappingEvent;
import com.gitee.sop.servercommon.manager.ServiceZookeeperApiMetaManager;
import com.gitee.sop.servercommon.mapping.ApiMappingHandlerMapping;
import com.gitee.sop.servercommon.message.ServiceErrorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;

/**
 * @author tanghc
 */
public class BaseServiceConfiguration extends WebMvcConfigurationSupport {

    public BaseServiceConfiguration() {
        ServiceConfig.getInstance().getI18nModules().add("i18n/isp/bizerror");
    }

    @Autowired
    private Environment environment;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        registry.addInterceptor(new ServiceContextInterceptor());
        super.addInterceptors(registry);
    }

    /**
     * 自定义Mapping，详见@ApiMapping
     * @return 返回RequestMappingHandlerMapping
     */
    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        ApiMetaManager apiMetaManager = getApiMetaManager(environment);
        RequestMappingEvent requestMappingEvent = getRequestMappingEvent(apiMetaManager, environment);
        return new ApiMappingHandlerMapping(requestMappingEvent);
    }

    protected RequestMappingEvent getRequestMappingEvent(ApiMetaManager apiMetaManager, Environment environment) {
        return new DefaultRequestMappingEvent(apiMetaManager, environment);
    }

    protected ApiMetaManager getApiMetaManager(Environment environment) {
        return new ServiceZookeeperApiMetaManager(environment);
    }

    @Bean
    GlobalExceptionHandler globalExceptionHandler() {
        return ServiceConfig.getInstance().getGlobalExceptionHandler();
    }

    @PostConstruct
    public final void after() {
        initMessage();
        doAfter();
    }

    protected void doAfter() {

    }

    protected void initMessage() {
        ServiceErrorFactory.initMessageSource(ServiceConfig.getInstance().getI18nModules());
    }

}