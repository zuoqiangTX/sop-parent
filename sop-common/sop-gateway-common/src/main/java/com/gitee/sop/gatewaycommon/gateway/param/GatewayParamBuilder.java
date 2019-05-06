package com.gitee.sop.gatewaycommon.gateway.param;

import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.param.BaseParamBuilder;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

/**
 * @author tanghc
 */
public class GatewayParamBuilder extends BaseParamBuilder<ServerWebExchange> {

    @Override
    public Map<String, String> buildRequestParams(ServerWebExchange request) {
        return request.getAttribute(SopConstants.CACHE_REQUEST_BODY_FOR_MAP);
    }
}
