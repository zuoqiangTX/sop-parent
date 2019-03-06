package com.gitee.sop.gatewaycommon.result;

import org.springframework.web.server.ServerWebExchange;

/**
 * 对返回结果进行处理
 * @author tanghc
 */
public interface ResultExecutor {
    /**
     * 合并结果
     * @param exchange
     * @param responseData
     * @return
     */
    String mergeResult(ServerWebExchange exchange, String responseData);

    /**
     * 合并错误结果
     * @param exchange
     * @param ex
     * @return
     */
    GatewayResult buildErrorResult(ServerWebExchange exchange, Throwable ex);
}
