package com.gitee.sop.gatewaycommon.param;

import com.gitee.sop.gatewaycommon.bean.BaseRouteDefinition;
import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import com.gitee.sop.gatewaycommon.manager.RouteRepositoryContext;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Map;

/**
 * @author tanghc
 */
@Slf4j
public abstract class BaseParamBuilder<T> implements ParamBuilder<T> {

    public abstract Map<String, String> buildRequestParams(T ctx);

    @Override
    public ApiParam build(T ctx) {
        ApiParam apiParam = this.newApiParam(ctx);
        Map<String, String> requestParams = this.buildRequestParams(ctx);
        for (Map.Entry<String, ?> entry : requestParams.entrySet()) {
            apiParam.put(entry.getKey(), entry.getValue());
        }
        this.initOtherProperty(apiParam);
        return apiParam;
    }

    protected ApiParam newApiParam(T ctx) {
        return new ApiParam();
    }

    protected void initOtherProperty(ApiParam apiParam) {
        if (apiParam.size() == 0) {
            throw ErrorEnum.ISV_INVALID_METHOD.getErrorMeta().getException();
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
        apiParam.setIgnoreValidate(BooleanUtils.toBoolean(routeDefinition.getIgnoreValidate()));
    }

}
