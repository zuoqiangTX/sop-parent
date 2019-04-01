package com.gitee.sop.gatewaycommon.zuul.filter;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.BaseRouteDefinition;
import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import com.gitee.sop.gatewaycommon.manager.IsvRoutePermissionManager;
import com.gitee.sop.gatewaycommon.manager.RouteRepositoryContext;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.zuul.ZuulContext;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.BooleanUtils;

/**
 * 路由权限校验，有些接口需要配置权限才能访问。
 * @author tanghc
 */
public class PreRoutePermissionFilter extends BaseZuulFilter {
    @Override
    protected FilterType getFilterType() {
        return FilterType.PRE;
    }

    @Override
    protected int getFilterOrder() {
        // 放在签名验证后面
        return PRE_VALIDATE_FILTER_ORDER + 1;
    }

    @Override
    protected Object doRun(RequestContext requestContext) throws ZuulException {
        ApiParam apiParam = ZuulContext.getApiParam();
        String routeId = apiParam.fetchNameVersion();
        TargetRoute targetRoute = RouteRepositoryContext.getRouteRepository().get(routeId);
        BaseRouteDefinition routeDefinition = targetRoute.getRouteDefinition();
        boolean needCheckPermission = BooleanUtils.toBoolean(routeDefinition.getPermission());
        if (needCheckPermission) {
            IsvRoutePermissionManager isvRoutePermissionManager = ApiConfig.getInstance().getIsvRoutePermissionManager();
            String appKey = apiParam.fetchAppKey();
            boolean hasPermission = isvRoutePermissionManager.hasPermission(appKey, routeId);
            if (!hasPermission) {
                throw ErrorEnum.ISV_ROUTE_NO_PERMISSIONS.getErrorMeta().getException();
            }
        }

        return null;
    }
}
