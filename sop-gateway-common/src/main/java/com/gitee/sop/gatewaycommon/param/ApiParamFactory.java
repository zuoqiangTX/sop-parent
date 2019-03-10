package com.gitee.sop.gatewaycommon.param;

import com.gitee.sop.gatewaycommon.bean.BaseRouteDefinition;
import com.gitee.sop.gatewaycommon.manager.RouteDefinitionItemContext;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;

import java.util.Map;

/**
 * @author tanghc
 */
public class ApiParamFactory {
    public static ApiParam build(Map<String, ?> params) {
        ApiParam apiParam = new ApiParam();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            apiParam.put(entry.getKey(), entry.getValue());
        }
        BaseRouteDefinition routeDefinition = RouteDefinitionItemContext.getRouteDefinition(apiParam.fetchNameVersion());
        if (routeDefinition == null) {
            throw ErrorEnum.ISV_INVALID_METHOD.getErrorMeta().getException();
        }
        apiParam.setIgnoreValidate(routeDefinition.isIgnoreValidate());
        return apiParam;
    }
}
