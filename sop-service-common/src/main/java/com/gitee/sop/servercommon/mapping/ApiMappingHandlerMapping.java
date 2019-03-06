package com.gitee.sop.servercommon.mapping;

import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.servercommon.annotation.ApiAbility;
import com.gitee.sop.servercommon.bean.ServiceConfig;
import com.gitee.sop.servercommon.manager.RequestMappingEvent;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @author tanghc
 */
public class ApiMappingHandlerMapping extends RequestMappingHandlerMapping implements PriorityOrdered {

    private static StringValueResolver stringValueResolver = new ApiMappingStringValueResolver();

    private RequestMappingEvent requestMappingEvent;

    public ApiMappingHandlerMapping(RequestMappingEvent requestMappingEvent) {
        this.requestMappingEvent = requestMappingEvent;
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        ApiMapping apiMapping = method.getAnnotation(ApiMapping.class);
        StringValueResolver valueResolver = null;
        if (apiMapping != null) {
            valueResolver = stringValueResolver;
        }
        this.setEmbeddedValueResolver(valueResolver);
        return super.getMappingForMethod(method, handlerType);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        method.setAccessible(true);
        String name = null;
        String version;
        ApiMapping apiMapping = method.getAnnotation(ApiMapping.class);
        if (apiMapping != null) {
            name = apiMapping.value()[0];
            version = apiMapping.version();
        } else {
            ApiAbility apiAbility = this.findApiAbilityAnnotation(method);
            if (apiAbility != null) {
                version = apiAbility.version();
            } else {
                return super.getCustomMethodCondition(method);
            }
        }
        if ("".equals(version)) {
            version = ServiceConfig.getInstance().getDefaultVersion();
        }
        ApiMappingInfo apiMappingInfo = new ApiMappingInfo(name, version);
        logger.info("注册接口，method:" + method + "， version:" + version);
        return new ApiMappingRequestCondition(apiMappingInfo);
    }

    protected ApiAbility findApiAbilityAnnotation(Method method) {
        ApiAbility apiAbility = method.getAnnotation(ApiAbility.class);
        if (apiAbility == null) {
            Class<?> controllerClass = method.getDeclaringClass();
            apiAbility = AnnotationUtils.findAnnotation(controllerClass, ApiAbility.class);
        }
        return apiAbility;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.requestMappingEvent.onRegisterSuccess(this);
    }
}