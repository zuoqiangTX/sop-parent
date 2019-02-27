package com.gitee.sop.gatewaycommon.result;

import com.gitee.sop.gatewaycommon.message.Error;

/**
 * @author tanghc
 */
public interface ResultBuilder {


    /**
     * 构建网关错误返回结果
     *
     * @param throwable 异常
     * @return 返回最终结果
     */
    Result buildGatewayError(Throwable throwable);

    /**
     * 构建网关错误返回结果
     *
     * @param error error
     * @return 返回最终结果
     */
    Result buildGatewayError(Error error);

}
