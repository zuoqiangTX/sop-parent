package com.gitee.sop.gatewaycommon.zuul.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.exception.ApiException;
import com.gitee.sop.gatewaycommon.message.Error;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.result.BaseExecutorAdapter;
import com.gitee.sop.gatewaycommon.zuul.ZuulContext;
import com.netflix.util.Pair;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author tanghc
 */
@Slf4j
public class ZuulResultExecutor extends BaseExecutorAdapter<RequestContext, String> {

    @Override
    public int getBizHeaderCode(RequestContext requestContext) {
        // 微服务端返回的head
        int code = HttpStatus.OK.value();
        List<Pair<String, String>> bizHeaders = requestContext.getZuulResponseHeaders();
        Optional<Pair<String, String>> first = bizHeaders.stream()
                .filter(header -> {
                    return SopConstants.X_BIZ_ERROR_CODE.equals(header.first());
                }).findFirst();

        Pair<String, String> header = first.orElseGet(() -> {
            return new Pair<String, String>(HttpStatus.OK.name(), String.valueOf(HttpStatus.OK.value()));
        });

        String bizErrorCode = header.second();
        if (bizErrorCode != null) {
            code = Integer.valueOf(bizErrorCode);
        }
        return code;
    }

    @Override
    public Map<String, ?> getApiParam(RequestContext requestContext) {
        return ZuulContext.getApiParam();
    }

    @Override
    public String buildErrorResult(RequestContext request, Throwable throwable) {
        Error error = getError(throwable);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(error);
        return this.merge(request, jsonObject);
    }

    public static Error getError(Throwable throwable) {
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
        return error;
    }
}
