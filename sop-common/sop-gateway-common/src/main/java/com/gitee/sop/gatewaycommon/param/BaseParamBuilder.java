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

    /**
     * 构建请求参数
     * @param ctx 请求request
     * @return 返回请求参数
     */
    public abstract Map<String, ?> buildRequestParams(T ctx);

    /**
     * 返回客户端ip
     * @param ctx  请求request
     * @return 返回ip
     */
    public abstract String getIP(T ctx);

    /**
     * 将版本号添加到header中
     * @param ctx 请求request
     * @param headerName headerName
     * @param version 版本号
     */
    public abstract void setVersionInHeader(T ctx, String headerName, String version);

    @Override
    public ApiParam build(T ctx) {
        ApiParam apiParam = this.newApiParam(ctx);
        Map<String, ?> requestParams = this.buildRequestParams(ctx);
        apiParam.putAll(requestParams);
        this.initOtherProperty(apiParam);
        apiParam.setIp(this.getIP(ctx));
        this.setVersionInHeader(ctx, ParamNames.HEADER_VERSION_NAME, apiParam.fetchVersion());
        return apiParam;
    }

    protected ApiParam newApiParam(T ctx) {
        return new ApiParam();
    }

    protected void initOtherProperty(ApiParam apiParam) {
        RouteRepository<? extends TargetRoute> routeRepository = RouteRepositoryContext.getRouteRepository();
        if (routeRepository == null) {
            log.error("RouteRepositoryContext.setRouteRepository()方法未使用");
            throw ErrorEnum.ISP_UNKNOWN_ERROR.getErrorMeta().getException();
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

