package com.gitee.sop.gatewaycommon.param;

import com.gitee.sop.gatewaycommon.bean.RouteDefinition;
import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import com.gitee.sop.gatewaycommon.manager.RouteRepositoryContext;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Optional;

/**
 * @author tanghc
 */
@Slf4j
public abstract class BaseParamBuilder<T> implements ParamBuilder<T> {

    /**
     * 构建请求参数
     *
     * @param ctx 请求request
     * @return 返回请求参数
     */
    public abstract ApiParam buildRequestParams(T ctx);

    /**
     * 返回客户端ip
     *
     * @param ctx 请求request
     * @return 返回ip
     */
    public abstract String getIP(T ctx);

    /**
     * 将版本号添加到header中
     *
     * @param ctx        请求request
     * @param headerName headerName
     * @param version    版本号
     */
    public abstract void setVersionInHeader(T ctx, String headerName, String version);

    @Override
    public ApiParam build(T ctx) {
        //获取原始请求参数
        ApiParam apiParam = this.buildRequestParams(ctx);
        //处理请求参数
        this.processApiParam(apiParam, ctx);
        //初始化其他属性
        this.initOtherProperty(apiParam);
        apiParam.setIp(this.getIP(ctx));
        //        将sop-version 设置到请求头中
        this.setVersionInHeader(ctx, ParamNames.HEADER_VERSION_NAME, apiParam.fetchVersion());
        return apiParam;
    }

    protected void processApiParam(ApiParam param, T ctx) {

    }

    protected void initOtherProperty(ApiParam apiParam) {
        //从服务缓存上下文拿到 所有服务的缓存【从路由缓存中拿到路由】
        RouteRepository<? extends TargetRoute> routeRepository = RouteRepositoryContext.getRouteRepository();
        if (routeRepository == null) {
            //没有设置 服务缓存
            log.error("RouteRepositoryContext.setRouteRepository()方法未使用");
            throw ErrorEnum.ISP_UNKNOWN_ERROR.getErrorMeta().getException();
        }

        //接口名+版本号
        String nameVersion = Optional.ofNullable(apiParam.fetchNameVersion()).orElse(String.valueOf(System.currentTimeMillis()));
        //目标路由
        TargetRoute targetRoute = routeRepository.get(nameVersion);
//        如果目标路由不存在
        Integer ignoreValidate = Optional.ofNullable(targetRoute)
                .map(TargetRoute::getRouteDefinition)
                .map(RouteDefinition::getIgnoreValidate)
                // 默认不忽略
                .orElse(BooleanUtils.toInteger(false));
        apiParam.setIgnoreValidate(BooleanUtils.toBoolean(ignoreValidate));
    }

}

