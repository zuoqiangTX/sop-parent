package com.gitee.sop.gatewaycommon.param;

import com.gitee.sop.gatewaycommon.bean.SopConstants;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

/**
 * @author tanghc
 */
public class ApiParamBuilder implements ParamBuilder {
    @Override
    public ApiParam build(ServerWebExchange exchange) {
        Map<String, String> params = exchange.getAttribute(SopConstants.CACHE_REQUEST_BODY_FOR_MAP);
        ApiParam apiParam = new ApiParam();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            apiParam.put(entry.getKey(), entry.getValue());
        }
        return apiParam;
    }


}
