package com.gitee.sop.servercommon.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanghc
 */
public interface ServiceResultBuilder {


    /**
     * 构建错误返回结果
     *
     * @param throwable 异常
     * @return 返回最终结果
     */
    Object buildError(HttpServletRequest request, HttpServletResponse response, Throwable throwable);


    /**
     * 构建错误返回结果
     * @param subCode
     * @param subMsg
     * @return
     */
    Object buildError(String subCode, String subMsg);

}
