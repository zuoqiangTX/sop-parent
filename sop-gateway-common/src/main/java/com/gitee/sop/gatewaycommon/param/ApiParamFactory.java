package com.gitee.sop.gatewaycommon.param;

import com.gitee.sop.gatewaycommon.bean.BaseRouteDefinition;
import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import com.gitee.sop.gatewaycommon.manager.RouteRepositoryContext;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author tanghc
 */
@Slf4j
public class ApiParamFactory {
    public static ApiParam build(Map<String, ?> params) {
        ApiParam apiParam = new ApiParam();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            apiParam.put(entry.getKey(), entry.getValue());
        }

        RouteRepository<? extends TargetRoute> routeRepository = RouteRepositoryContext.getRouteRepository();
        if (routeRepository == null) {
            log.error("RouteRepositoryContext.setRouteRepository()方法未使用");
            throw ErrorEnum.AOP_UNKNOW_ERROR.getErrorMeta().getException();
        }
        TargetRoute targetRoute = routeRepository.get(apiParam.fetchNameVersion());
        BaseRouteDefinition routeDefinition = targetRoute.getRouteDefinition();
        if (routeDefinition == null) {
            throw ErrorEnum.ISV_INVALID_METHOD.getErrorMeta().getException();
        }
        apiParam.setIgnoreValidate(routeDefinition.isIgnoreValidate());
        return apiParam;
    }
}
