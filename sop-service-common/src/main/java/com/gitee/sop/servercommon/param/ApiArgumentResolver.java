package com.gitee.sop.servercommon.param;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.servercommon.bean.ParamNames;
import lombok.Data;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 解析request参数中的业务参数，隐射到方法参数上
 * @author tanghc
 */
@Data
public class ApiArgumentResolver implements HandlerMethodArgumentResolver {

    private ParamValidator paramValidator = new ServiceParamValidator();

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter != null;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String bizContent = nativeWebRequest.getParameter(ParamNames.BIZ_CONTENT_NAME);
        if (bizContent != null) {
            Class<?> parameterType = methodParameter.getParameterType();
            Object paramObj = JSON.parseObject(bizContent, parameterType);
            // JSR-303验证
            paramValidator.validateBizParam(paramObj);
            return paramObj;
        }
        return null;
    }

}