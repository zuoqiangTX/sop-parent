package com.gitee.sop.gatewaycommon.zuul.filter;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ConfigLimitDto;
import com.gitee.sop.gatewaycommon.bean.RouteConfig;
import com.gitee.sop.gatewaycommon.exception.ApiException;
import com.gitee.sop.gatewaycommon.limit.LimitManager;
import com.gitee.sop.gatewaycommon.limit.LimitType;
import com.gitee.sop.gatewaycommon.manager.LimitConfigManager;
import com.gitee.sop.gatewaycommon.message.ErrorImpl;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.util.RequestUtil;
import com.gitee.sop.gatewaycommon.zuul.ZuulContext;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    protected Object doRun(RequestContext requestContext) throws ZuulException
    {
        ApiConfig apiConfig = ApiConfig.getInstance();
        // 限流功能未开启，直接返回
        if (!apiConfig.isOpenLimit()) {
            return null;
        }
        ApiParam apiParam = ZuulContext.getApiParam();
        ConfigLimitDto configLimitDto = this.findConfigLimitDto(apiConfig, apiParam, requestContext.getRequest());
        if (configLimitDto == null) {
            return null;
        }
        // 单个限流功能未开启
        if (configLimitDto.getLimitStatus() == RouteConfig.LIMIT_STATUS_CLOSE) {
            return null;
        }
        byte limitType = configLimitDto.getLimitType().byteValue();
        LimitManager limitManager = ApiConfig.getInstance().getLimitManager();
        // 如果是漏桶策略
        if (limitType == LimitType.LEAKY_BUCKET.getType()) {
            boolean acquire = limitManager.acquire(configLimitDto);
            if (!acquire) {
                throw new ApiException(new ErrorImpl(configLimitDto.getLimitCode(), configLimitDto.getLimitMsg()));
            }
        } else if (limitType == LimitType.TOKEN_BUCKET.getType()) {
            limitManager.acquireToken(configLimitDto);
        }
        return null;
    }

    protected ConfigLimitDto findConfigLimitDto(ApiConfig apiConfig, ApiParam apiParam, HttpServletRequest request) {
        LimitConfigManager limitConfigManager = apiConfig.getLimitConfigManager();

        String routeId = apiParam.fetchNameVersion();
        String appKey = apiParam.fetchAppKey();
        String ip = RequestUtil.getIP(request);

        String[] limitKeys = new String[]{
                routeId,
                appKey,
                routeId + appKey,

                ip + routeId,
                ip + appKey,
                ip + routeId + appKey,
        };

        List<ConfigLimitDto> limitConfigList = new ArrayList<>();
        for (String limitKey : limitKeys) {
            ConfigLimitDto configLimitDto = limitConfigManager.get(limitKey);
            limitConfigList.add(configLimitDto);
        }
        if (limitConfigList.isEmpty()) {
            return null;
        }
        Collections.sort(limitConfigList, Comparator.comparing(ConfigLimitDto::getOrderIndex));
        return limitConfigList.get(0);
    }
}
