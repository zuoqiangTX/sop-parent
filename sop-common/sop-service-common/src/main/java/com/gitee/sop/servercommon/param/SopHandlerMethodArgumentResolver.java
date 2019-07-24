package com.gitee.sop.servercommon.param;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * @author tanghc
 */
public interface SopHandlerMethodArgumentResolver extends HandlerMethodArgumentResolver {
    void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter requestMappingHandlerAdapter);
}
