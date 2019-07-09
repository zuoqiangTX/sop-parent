package com.gitee.sop.gatewaycommon.gateway;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.util.RequestUtil;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gitee.sop.gatewaycommon.bean.SopConstants.CACHE_REQUEST_BODY_FOR_MAP;
import static com.gitee.sop.gatewaycommon.bean.SopConstants.CACHE_REQUEST_BODY_OBJECT_KEY;

/**
 * @author tanghc
 */
public class GatewayContext extends ApiContext {

    /**
     * 获取请求参数
     * @param exchange
     * @return
     */
    public static ApiParam getApiParam(ServerWebExchange exchange) {
        return exchange.getAttribute(SopConstants.CACHE_API_PARAM);
    }

    /**
     * 设置请求参数
     * @param exchange
     * @param apiParam
     */
    public static void setApiParam(ServerWebExchange exchange, ApiParam apiParam) {
        exchange.getAttributes().put(SopConstants.CACHE_API_PARAM, apiParam);
    }

    /**
     * 获取Spring Cloud Gateway请求的原始参数。前提是要使用ReadBodyRoutePredicateFactory
     * @param exchange
     * @return 没有参数返回null
     * @see com.gitee.sop.gatewaycommon.gateway.route.ReadBodyRoutePredicateFactory
     */
    public static Map<String, String> getRequestParams(ServerWebExchange exchange) {
        Map<String, String> params = exchange.getAttribute(CACHE_REQUEST_BODY_FOR_MAP);
        if (params != null) {
            return params;
        }
        if (exchange.getRequest().getMethod() == HttpMethod.GET) {
            MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
            params = buildParams(queryParams);
        } else {
            String cachedBody = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
            if (cachedBody != null) {
                params = RequestUtil.parseQueryToMap(cachedBody);
            }
        }
        if (params != null) {
            exchange.getAttributes().put(CACHE_REQUEST_BODY_FOR_MAP, params);
        }
        return params;
    }

    private static Map<String, String> buildParams(MultiValueMap<String, String> queryParams) {
        if (queryParams == null || queryParams.size() == 0) {
            return null;
        }
        Map<String, String> params = new HashMap<>(queryParams.size());
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            String val = entry.getValue().get(0);
            params.put(entry.getKey(), val);
        }
        return params;
    }
}
