package com.gitee.sop.servercommon.configuration;

import com.gitee.sop.servercommon.bean.ServiceConfig;
import com.gitee.sop.servercommon.interceptor.ServiceContextInterceptor;
import com.gitee.sop.servercommon.manager.ApiMetaManager;
import com.gitee.sop.servercommon.manager.DefaultRequestMappingEvent;
import com.gitee.sop.servercommon.manager.RequestMappingEvent;
import com.gitee.sop.servercommon.manager.ServiceZookeeperApiMetaManager;
import com.gitee.sop.servercommon.mapping.ApiMappingHandlerMapping;
import com.gitee.sop.servercommon.message.ServiceErrorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author tanghc
 */
@Slf4j
public class BaseServiceConfiguration extends WebMvcConfigurationSupport
        implements ApplicationRunner {

    public BaseServiceConfiguration() {
        ServiceConfig.getInstance().getI18nModules().add("i18n/isp/bizerror");
    }

    @Autowired
    private Environment environment;

    private ApiMappingHandlerMapping apiMappingHandlerMapping = new ApiMappingHandlerMapping();

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        // 支持swagger-bootstrap-ui首页
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        // 支持默认swagger
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        registry.addInterceptor(new ServiceContextInterceptor());
        super.addInterceptors(registry);
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 解决controller返回字符串中文乱码问题
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter)converter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * 自定义Mapping，详见@ApiMapping
     * @return 返回RequestMappingHandlerMapping
     */
    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return apiMappingHandlerMapping;
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
        log.info("-----spring容器加载完毕-----");
        initMessage();
        doAfter();
    }

    /**
     * springboot启动完成后执行
     * @param args 启动参数
     * @throws Exception 出错异常
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("-----服务器启动完毕-----");
        ApiMetaManager apiMetaManager = getApiMetaManager(environment);
        RequestMappingEvent requestMappingEvent = getRequestMappingEvent(apiMetaManager, environment);
        requestMappingEvent.onRegisterSuccess(apiMappingHandlerMapping);
        this.onStartup(args);
    }

    /**
     * spring容器加载完毕后执行
     */
    protected void doAfter() {

    }

    /**
     * 启动完毕后执行
     * @param args
     */
    protected void onStartup(ApplicationArguments args) {

    }

    protected void initMessage() {
        ServiceErrorFactory.initMessageSource(ServiceConfig.getInstance().getI18nModules());
    }

}
