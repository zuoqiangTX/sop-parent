package com.gitee.sop.servercommon.param;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.servercommon.annotation.ApiAbility;
import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.servercommon.bean.ParamNames;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 解析request参数中的业务参数，绑定到方法参数上
 *
 * @author tanghc
 */
public class ApiArgumentResolver implements SopHandlerMethodArgumentResolver {

    private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache =
            new ConcurrentHashMap<>(256);

    private final ParamValidator paramValidator = new ServiceParamValidator();

    private List<HandlerMethodArgumentResolver> argumentResolvers = Collections.emptyList();

    @Override
    public void setResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        this.argumentResolvers = resolvers;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        boolean hasAnnotation = methodParameter.getMethodAnnotation(ApiMapping.class) != null
                || methodParameter.getMethodAnnotation(ApiAbility.class) != null;
        // 有注解
        return hasAnnotation;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        Object paramObj = this.getParamObject(methodParameter, nativeWebRequest);
        if (paramObj != null) {
            // JSR-303验证
            paramValidator.validateBizParam(paramObj);
            return paramObj;
        } else {
            HandlerMethodArgumentResolver resolver = getOtherArgumentResolver(methodParameter);
            if (resolver != null) {
                return resolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
            }
            return null;
        }
    }

    /**
     * 获取参数对象，将request中的参数绑定到实体对象中去
     *
     * @param methodParameter
     * @param nativeWebRequest
     * @return 没有返回null
     */
    protected Object getParamObject(MethodParameter methodParameter, NativeWebRequest nativeWebRequest) {
        String bizContent = nativeWebRequest.getParameter(ParamNames.BIZ_CONTENT_NAME);
        Object bizObj = null;
        if (bizContent != null) {
            Class<?> parameterType = methodParameter.getParameterType();
            bizObj = JSON.parseObject(bizContent, parameterType);
        }
        this.bindUploadFile(bizObj, nativeWebRequest);
        return bizObj;
    }

    /**
     * 将上传文件对象绑定到属性中
     *
     * @param bizObj           业务参数
     * @param nativeWebRequest
     */
    protected void bindUploadFile(Object bizObj, NativeWebRequest nativeWebRequest) {
        if (bizObj == null) {
            return;
        }
        Object nativeRequest = nativeWebRequest.getNativeRequest();
        if (nativeRequest instanceof StandardMultipartHttpServletRequest) {
            StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest) nativeRequest;
            Class<?> bizClass = bizObj.getClass();
            ReflectionUtils.doWithFields(bizClass, field -> {
                ReflectionUtils.makeAccessible(field);
                String name = field.getName();
                MultipartFile multipartFile = request.getFile(name);
                ReflectionUtils.setField(field, bizObj, multipartFile);
            }, field -> field.getType() == MultipartFile.class);
        }
    }

    protected HandlerMethodArgumentResolver getOtherArgumentResolver(MethodParameter parameter) {
        HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
        if (result == null) {
            for (HandlerMethodArgumentResolver methodArgumentResolver : this.argumentResolvers) {
                if (methodArgumentResolver instanceof SopHandlerMethodArgumentResolver) {
                    continue;
                }
                if (methodArgumentResolver.supportsParameter(parameter)) {
                    result = methodArgumentResolver;
                    this.argumentResolverCache.put(parameter, result);
                    break;
                }
            }
        }
        return result;
    }

}