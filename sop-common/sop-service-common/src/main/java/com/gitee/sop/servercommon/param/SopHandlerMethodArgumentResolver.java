package com.gitee.sop.servercommon.param;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.List;

/**
 * @author tanghc
 */
public interface SopHandlerMethodArgumentResolver extends HandlerMethodArgumentResolver {
    void setResolvers(List<HandlerMethodArgumentResolver> resolvers);
}
