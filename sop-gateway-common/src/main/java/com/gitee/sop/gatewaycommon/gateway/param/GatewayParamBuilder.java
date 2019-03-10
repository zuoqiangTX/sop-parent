package com.gitee.sop.gatewaycommon.gateway.param;

import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.param.ApiParamFactory;
import com.gitee.sop.gatewaycommon.param.ParamBuilder;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

/**
 * @author tanghc
 */
public class GatewayParamBuilder implements ParamBuilder<ServerWebExchange> {
    @Override
    public ApiParam build(ServerWebExchange exchange) {
        Map<String, String> params = exchange.getAttribute(SopConstants.CACHE_REQUEST_BODY_FOR_MAP);
        return ApiParamFactory.build(params);
    }


}
