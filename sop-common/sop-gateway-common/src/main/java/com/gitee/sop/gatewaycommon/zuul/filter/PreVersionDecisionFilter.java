package com.gitee.sop.gatewaycommon.zuul.filter;

import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import com.gitee.sop.gatewaycommon.manager.EnvGrayManager;
import com.gitee.sop.gatewaycommon.manager.RouteRepositoryContext;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.param.ParamNames;
import com.gitee.sop.gatewaycommon.zuul.ZuulContext;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author tanghc
 */
public class PreVersionDecisionFilter extends BaseZuulFilter {

    @Autowired
    private EnvGrayManager envGrayManager;

    @Override
    protected FilterType getFilterType() {
        return FilterType.PRE;
    }

    @Override
    protected int getFilterOrder() {
        return PRE_LIMIT_FILTER_ORDER + 1;
    }

    @Override
    protected Object doRun(RequestContext requestContext) throws ZuulException {
        ApiParam apiParam = ZuulContext.getApiParam();
        String nameVersion = apiParam.fetchNameVersion();
        TargetRoute targetRoute = RouteRepositoryContext.getRouteRepository().get(nameVersion);
        if (targetRoute == null) {
            return null;
        }
        String serviceId = targetRoute.getServiceRouteInfo().getServiceId();
        List<String> instanceIdList = envGrayManager.listGrayInstanceId(serviceId);

        for (String instanceId : instanceIdList) {
            String version = envGrayManager.getVersion(instanceId, nameVersion);
            if (version != null) {
                requestContext.addZuulRequestHeader(ParamNames.HEADER_VERSION_NAME, version);
                break;
            }
        }
        return null;
    }
}
