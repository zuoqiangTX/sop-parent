package com.gitee.sop.gatewaycommon.zuul.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.exception.ApiException;
import com.gitee.sop.gatewaycommon.message.Error;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.result.BaseExecutorAdapter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author tanghc
 */
@Slf4j
public class ZuulResultExecutor extends BaseExecutorAdapter<RequestContext, String> {

    @Override
    public int getBizHeaderCode(RequestContext requestContext) {
        HttpServletResponse response = requestContext.getResponse();
        int code = HttpStatus.OK.value();
        String bizErrorCode = response.getHeader(SopConstants.X_BIZ_ERROR_CODE);
        if (bizErrorCode != null) {
            code = Integer.valueOf(bizErrorCode);
        }
        return code;
    }

    @Override
    public Map<String, ?> getApiParam(RequestContext requestContext) {
        return (Map<String, ?>) requestContext.get(SopConstants.CACHE_API_PARAM);
    }

    @Override
    public String buildErrorResult(RequestContext request, Throwable throwable) {
        Error error = null;
        if (throwable instanceof ZuulException) {
            ZuulException ex = (ZuulException) throwable;
            Throwable cause = ex.getCause();
            if (cause instanceof ApiException) {
                ApiException apiException = (ApiException) cause;
                error = apiException.getError();
            }
        }
        if (error == null) {
            error = ErrorEnum.AOP_UNKNOW_ERROR.getErrorMeta().getError();
        }
        JSONObject jsonObject = (JSONObject) JSON.toJSON(error);
        return this.merge(request, jsonObject);
    }

}
