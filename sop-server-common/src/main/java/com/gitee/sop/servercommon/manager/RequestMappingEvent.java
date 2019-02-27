package com.gitee.sop.servercommon.manager;

import com.gitee.sop.servercommon.mapping.ApiMappingHandlerMapping;

/**
 * @author tanghc
 */
public interface RequestMappingEvent {
    void onRegisterSuccess(ApiMappingHandlerMapping apiMappingHandlerMapping);
}
