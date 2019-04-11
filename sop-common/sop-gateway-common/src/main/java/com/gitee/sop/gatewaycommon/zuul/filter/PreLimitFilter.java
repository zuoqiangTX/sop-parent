package com.gitee.sop.gatewaycommon.zuul.filter;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.RouteConfig;
import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import com.gitee.sop.gatewaycommon.exception.ApiException;
import com.gitee.sop.gatewaycommon.limit.LimitManager;
import com.gitee.sop.gatewaycommon.limit.LimitType;
import com.gitee.sop.gatewaycommon.manager.RouteConfigManager;
import com.gitee.sop.gatewaycommon.manager.RouteRepositoryContext;
import com.gitee.sop.gatewaycommon.message.ErrorImpl;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.zuul.ZuulContext;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 限流拦截器
 * @author tanghc
 */
public class PreLimitFilter extends BaseZuulFilter {
    @Override
    protected FilterType getFilterType() {
        return FilterType.PRE;
    }

    @Override
    protected int getFilterOrder() {
        return PRE_LIMIT_FILTER_ORDER;
    }

    @Override
    protected Object doRun(RequestContext requestContext) throws ZuulException {
        ApiConfig apiConfig = ApiConfig.getInstance();
        // 限流功能未开启，直接返回
        if (!apiConfig.isOpenLimit()) {
            return null;
        }
        ApiParam apiParam = ZuulContext.getApiParam();
        String routeId = apiParam.getRouteId();
        RouteConfigManager routeConfigManager = apiConfig.getRouteConfigManager();
        RouteConfig routeConfig = routeConfigManager.get(routeId);
        if (routeConfig == null) {
            return null;
        }
        // 某个路由限流功能未开启
        if (routeConfig.getLimitStatus() == RouteConfig.LIMIT_STATUS_CLOSE) {
            return null;
        }
        byte limitType = routeConfig.getLimitType().byteValue();
        LimitManager limitManager = ApiConfig.getInstance().getLimitManager();
        if (limitType == LimitType.LEAKY_BUCKET.getType()) {
            boolean acquire = limitManager.acquire(routeConfig);
            if (!acquire) {
                throw new ApiException(new ErrorImpl(routeConfig.getLimitCode(), routeConfig.getLimitMsg()));
            }
        } else if (limitType == LimitType.TOKEN_BUCKET.getType()) {
            limitManager.acquireToken(routeConfig);
        }
        return null;
    }
}
