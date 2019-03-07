package com.gitee.sop.gatewaycommon.result;

import com.gitee.sop.gatewaycommon.gateway.result.GatewayResult;

/**
 * 对返回结果进行处理
 * 成功示例
 * {
 * "alipay_trade_fastpay_refund_query_response": {
 * "code": "10000",
 * "msg": "Success",
 * "trade_no": "2014112611001004680073956707",
 * "out_trade_no": "20150320010101001",
 * "out_request_no": "20150320010101001",
 * "refund_reason": "用户退款请求",
 * "total_amount": 100.2,
 * "refund_amount": 12.33
 * },
 * "sign": "ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE"
 * }
 * <p>
 * 异常示例
 * {
 * "alipay_trade_fastpay_refund_query_response": {
 * "code": "20000",
 * "msg": "Service Currently Unavailable",
 * "sub_code": "isp.unknow-error",
 * "sub_msg": "系统繁忙"
 * },
 * "sign": "ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE"
 * }
 *
 * @author tanghc
 */
public interface ResultExecutor<T, R> {
    /**
     * 合并结果
     * @param request
     * @param serviceResult
     * @return
     */
    String mergeResult(T request, String serviceResult);

    /**
     * 合并错误结果
     * @param request
     * @param ex
     * @return
     */
    R buildErrorResult(T request, Throwable ex);
}
