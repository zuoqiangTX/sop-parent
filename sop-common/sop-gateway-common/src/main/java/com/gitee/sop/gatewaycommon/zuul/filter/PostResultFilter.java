package com.gitee.sop.gatewaycommon.zuul.filter;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.result.ResultExecutor;
import com.netflix.util.Pair;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.util.List;

/**
 * 合并微服务结果，统一返回格式
 *
 * @author tanghc
 */
public class PostResultFilter extends BaseZuulFilter {

    @Override
    protected FilterType getFilterType() {
        return FilterType.POST;
    }

    @Override
    protected int getFilterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }

    @Override
    protected Object doRun(RequestContext requestContext) throws ZuulException {
        if (requestContext.getResponse().isCommitted()) {
            return null;
        }
        List<Pair<String, String>> zuulResponseHeaders = requestContext.getZuulResponseHeaders();
        boolean isDownloadRequest = zuulResponseHeaders
                .stream()
                .anyMatch(pair -> StringUtils.contains(pair.second(), MediaType.APPLICATION_OCTET_STREAM_VALUE));
        // 如果是文件下载直接返回
        if (isDownloadRequest) {
            return null;
        }
        InputStream responseDataStream = requestContext.getResponseDataStream();
        ApiConfig apiConfig = ApiContext.getApiConfig();
        ResultExecutor<RequestContext, String> resultExecutor = apiConfig.getZuulResultExecutor();
        String serviceResult;
        try {
            serviceResult = IOUtils.toString(responseDataStream, SopConstants.CHARSET_UTF8);
        } catch (Exception e) {
            log.error("业务方无数据返回", e);
            serviceResult = SopConstants.EMPTY_JSON;
        }
        String finalResult = resultExecutor.mergeResult(requestContext, serviceResult);
        requestContext.setResponseBody(finalResult);
        return null;
    }

}
