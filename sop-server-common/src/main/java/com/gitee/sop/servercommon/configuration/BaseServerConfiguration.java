package com.gitee.sop.servercommon.configuration;

import com.gitee.sop.servercommon.bean.ServiceConfig;
import com.gitee.sop.servercommon.exception.ServiceException;
import com.gitee.sop.servercommon.interceptor.ServiceContextInterceptor;
import com.gitee.sop.servercommon.manager.ApiMetaManager;
import com.gitee.sop.servercommon.manager.DefaultRequestMappingEvent;
import com.gitee.sop.servercommon.manager.RedisApiMetaManager;
import com.gitee.sop.servercommon.mapping.ApiMappingHandlerMapping;
import com.gitee.sop.servercommon.message.ServiceErrorFactory;
import com.gitee.sop.servercommon.result.ServiceResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanghc
 */
public class BaseServerConfiguration extends WebMvcConfigurationSupport {

    public BaseServerConfiguration() {
        ServiceConfig.getInstance().getI18nModules().add("i18n/isp/bizerror");
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Environment environment;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        registry.addInterceptor(new ServiceContextInterceptor());
        super.addInterceptors(registry);
    }

    // 自定义Mapping，详见@ApiMapping
    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        ApiMetaManager apiMetaManager = new RedisApiMetaManager(redisTemplate);
        DefaultRequestMappingEvent requestMappingEvent = new DefaultRequestMappingEvent(apiMetaManager, environment);
        return new ApiMappingHandlerMapping(requestMappingEvent);
    }

    @Bean
    GlobalExceptionHandler globalExceptionHandler() {
        return ServiceConfig.getInstance().getGlobalExceptionHandler();
    }

    @PostConstruct
    public void after() {
        doAfter();
    }

    protected void doAfter() {
        initMessage();
    }

    protected void initMessage() {
        ServiceErrorFactory.initMessageSource(ServiceConfig.getInstance().getI18nModules());
    }

}
