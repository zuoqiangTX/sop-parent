package com.gitee.sop.servercommon.manager;

import com.gitee.sop.servercommon.mapping.ApiMappingHandlerMapping;

/**
 * @author tanghc
 */
public interface RequestMappingEvent {
    /**
     * 注册成功后回调
     * @param apiMappingHandlerMapping
     */
    void onRegisterSuccess(ApiMappingHandlerMapping apiMappingHandlerMapping);
}
