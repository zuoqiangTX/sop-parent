package com.gitee.sop.servercommon.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.servercommon.annotation.ApiAbility;
import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.servercommon.bean.ParamNames;
import lombok.Data;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.util.Map;

/**
 * 解析request参数中的业务参数，绑定到方法参数上
 *
 * @author tanghc
 */
@Data
public class ApiArgumentResolver implements HandlerMethodArgumentResolver {

    private ParamValidator paramValidator = new ServiceParamValidator();

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
        // JSR-303验证
        paramValidator.validateBizParam(paramObj);
        return paramObj;
    }

    /**
     * 获取参数对象，将request中的参数绑定到实体对象中去
     * @param methodParameter
     * @param nativeWebRequest
     * @return
     */
    protected Object getParamObject(MethodParameter methodParameter, NativeWebRequest nativeWebRequest) {
        String bizContent = nativeWebRequest.getParameter(ParamNames.BIZ_CONTENT_NAME);
        Class<?> parameterType = methodParameter.getParameterType();
        Object bizObj = null;
        if (bizContent != null) {
            bizObj = JSON.parseObject(bizContent, parameterType);
        } else {
            Map<String, String[]> parameterMap = nativeWebRequest.getParameterMap();
            JSONObject result = new JSONObject();
            parameterMap.forEach((key, values) -> {
                if (values.length > 0) {
                    result.put(key, values[0]);
                }
            });
            if (result.size() > 0) {
                bizObj = result.toJavaObject(parameterType);
            }
        }
        this.bindUploadFile(bizObj, nativeWebRequest);
        return bizObj;
    }

    /**
     * 将文件绑定到
     * @param bizObj 业务参数
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

}