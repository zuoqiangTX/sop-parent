package com.gitee.sop.gatewaycommon.result;

/**
 * @author tanghc
 */
public interface ResultExecutor {
    String mergeResult(int responseStatus, String responseData);

    String mergeError(Throwable throwable);
}
