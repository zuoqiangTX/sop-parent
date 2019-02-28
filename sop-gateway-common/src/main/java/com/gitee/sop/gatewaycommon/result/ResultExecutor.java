package com.gitee.sop.gatewaycommon.result;

/**
 * @author tanghc
 */
public interface ResultExecutor {
    /**
     * 合并结果
     * @param responseStatus
     * @param responseData
     * @return
     */
    String mergeResult(int responseStatus, String responseData);

    /**
     * 合并错误结果
     * @param throwable
     * @return
     */
    String mergeError(Throwable throwable);
}
