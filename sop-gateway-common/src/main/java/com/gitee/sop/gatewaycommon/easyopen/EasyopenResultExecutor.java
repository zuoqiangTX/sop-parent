package com.gitee.sop.gatewaycommon.easyopen;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gatewaycommon.message.Error;
import com.gitee.sop.gatewaycommon.result.ApiResult;
import com.gitee.sop.gatewaycommon.result.ResultExecutor;
import com.gitee.sop.gatewaycommon.zuul.result.ZuulResultExecutor;
import com.netflix.zuul.context.RequestContext;

/**
 * @author tanghc
 */
public class EasyopenResultExecutor implements ResultExecutor<RequestContext, String> {
    @Override
    public String mergeResult(RequestContext request, String serviceResult) {
        return serviceResult;
    }

    @Override
    public String buildErrorResult(RequestContext request, Throwable ex) {
        ApiResult apiResult = new ApiResult();
        Error error = ZuulResultExecutor.getError(ex);
        apiResult.setCode(error.getSub_code());
        apiResult.setMsg(error.getSub_msg());
        return JSON.toJSONString(apiResult);
    }
}
