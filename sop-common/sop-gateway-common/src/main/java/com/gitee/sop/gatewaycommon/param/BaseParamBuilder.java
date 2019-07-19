package com.gitee.sop.gatewaycommon.param;

import com.gitee.sop.gatewaycommon.bean.BaseRouteDefinition;
import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import com.gitee.sop.gatewaycommon.manager.RouteRepositoryContext;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @author tanghc
 */
@Slf4j
public abstract class BaseParamBuilder<T> implements ParamBuilder<T> {

    public abstract Map<String, String> buildRequestParams(T ctx);

    public abstract String getIP(T ctx);

    @Override
    public ApiParam build(T ctx) {
        ApiParam apiParam = this.newApiParam(ctx);
        Map<String, String> requestParams = this.buildRequestParams(ctx);
        for (Map.Entry<String, ?> entry : requestParams.entrySet()) {
            apiParam.put(entry.getKey(), entry.getValue());
        }
        this.initOtherProperty(apiParam);
        apiParam.setIp(this.getIP(ctx));
        return apiParam;
    }

    protected ApiParam newApiParam(T ctx) {
        return new ApiParam();
    }

    protected void initOtherProperty(ApiParam apiParam) {
        RouteRepository<? extends TargetRoute> routeRepository = RouteRepositoryContext.getRouteRepository();
        if (routeRepository == null) {
            log.error("RouteRepositoryContext.setRouteRepository()方法未使用");
            throw ErrorEnum.AOP_UNKNOW_ERROR.getErrorMeta().getException();
        }

        String nameVersion = Optional.ofNullable(apiParam.fetchNameVersion()).orElse(String.valueOf(System.currentTimeMillis()));
        TargetRoute targetRoute = routeRepository.get(nameVersion);
        Integer ignoreValidate = Optional.ofNullable(targetRoute)
                .map(t -> t.getRouteDefinition())
                .map(BaseRouteDefinition::getIgnoreValidate)
                // 默认不忽略
                .orElse(BooleanUtils.toInteger(false));
        apiParam.setIgnoreValidate(BooleanUtils.toBoolean(ignoreValidate));
    }

}

