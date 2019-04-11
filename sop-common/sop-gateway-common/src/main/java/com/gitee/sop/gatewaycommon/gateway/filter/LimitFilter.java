package com.gitee.sop.gatewaycommon.gateway.filter;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.bean.RouteConfig;
import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.exception.ApiException;
import com.gitee.sop.gatewaycommon.limit.LimitManager;
import com.gitee.sop.gatewaycommon.limit.LimitType;
import com.gitee.sop.gatewaycommon.manager.RouteConfigManager;
import com.gitee.sop.gatewaycommon.message.ErrorImpl;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.param.ParamNames;
import com.gitee.sop.gatewaycommon.util.RouteUtil;
import com.gitee.sop.gatewaycommon.validate.Validator;
import com.gitee.sop.gatewaycommon.zuul.ZuulContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author tanghc
 */
@Slf4j
public class LimitFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ApiConfig apiConfig = ApiConfig.getInstance();
        // 限流功能未开启，直接返回
        if (!apiConfig.isOpenLimit()) {
            return chain.filter(exchange);
        }
        Map<String, ?> apiParam = exchange.getAttribute(SopConstants.CACHE_API_PARAM);
        String routeId = apiParam.get(ParamNames.API_NAME).toString() + apiParam.get(ParamNames.VERSION_NAME);
        RouteConfigManager routeConfigManager = apiConfig.getRouteConfigManager();
        RouteConfig routeConfig = routeConfigManager.get(routeId);
        if (routeConfig == null) {
            return chain.filter(exchange);
        }
        // 某个路由限流功能未开启
        if (routeConfig.getLimitStatus() == RouteConfig.LIMIT_STATUS_CLOSE) {
            return chain.filter(exchange);
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
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Orders.LIMIT_ORDER;
    }
}
