package com.gitee.sop.gatewaycommon.param;

import org.springframework.web.server.ServerWebExchange;

/**
 * @author tanghc
 */
public interface ParamBuilder {
    /**
     * 从request提取参数
     * @param exchange
     * @return 返回ApiParam
     * @throws Exception
     */
    ApiParam build(ServerWebExchange exchange);
}
