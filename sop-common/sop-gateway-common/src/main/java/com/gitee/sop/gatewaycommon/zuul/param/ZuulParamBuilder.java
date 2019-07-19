package com.gitee.sop.gatewaycommon.zuul.param;

import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.param.BaseParamBuilder;
import com.gitee.sop.gatewaycommon.util.RequestUtil;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 参数解析默认实现
 *
 * @author tanghc
 */
@Slf4j
public class ZuulParamBuilder extends BaseParamBuilder<RequestContext> {

    private static final String CONTENT_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;
    private static final String CONTENT_TYPE_TEXT = MediaType.TEXT_PLAIN_VALUE;
    private static final String GET = "get";

    @Override
    public Map<String, String> buildRequestParams(RequestContext ctx) {
        HttpServletRequest request = ctx.getRequest();
        Map<String, String> params = null;

        if (GET.equalsIgnoreCase(request.getMethod())) {
            params = RequestUtil.convertRequestParamsToMap(request);
        } else {
            String contentType = request.getContentType();

            if (contentType == null) {
                contentType = "";
            }

            contentType = contentType.toLowerCase();

            // json或者纯文本形式
            if (contentType.contains(CONTENT_TYPE_JSON) || contentType.contains(CONTENT_TYPE_TEXT)) {
                throw ErrorEnum.ISV_INVALID_CONTENT_TYPE.getErrorMeta().getException();
            } else if (ServletFileUpload.isMultipartContent(request)) {
                MultipartRequestWrapper wrapper = new MultipartRequestWrapper(request);
                ctx.setRequest(wrapper);
                ctx.getZuulRequestHeaders().put("content-type", wrapper.getContentType());
                params = RequestUtil.convertMultipartRequestToMap(wrapper);
            } else {
                params = RequestUtil.convertRequestParamsToMap(request);
            }
        }

        return params;
    }

    @Override
    public String getIP(RequestContext ctx) {
        return RequestUtil.getIP(ctx.getRequest());
    }

    @Override
    protected ApiParam newApiParam(RequestContext ctx) {
        ApiParam apiParam = super.newApiParam(ctx);
        HttpServletRequest request = ctx.getRequest();
        String method = (String) request.getAttribute(SopConstants.REDIRECT_METHOD_KEY);
        String version = (String) request.getAttribute(SopConstants.REDIRECT_VERSION_KEY);
        apiParam.setRestName(method);
        apiParam.setRestVersion(version);
        return apiParam;
    }

    public static class MultipartRequestWrapper extends HttpServletRequestWrapper {
        private HttpServletRequest request;

        private volatile byte[] contentData;

        private MediaType contentType;

        private int contentLength;

        public MultipartRequestWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
            try {
                this.contentType = MediaType.valueOf(this.request.getContentType());
                byte[] input = IOUtils.toByteArray(request.getInputStream());
                this.contentData = input;
                this.contentLength = input.length;
            } catch (Exception e) {
                throw new IllegalStateException("Cannot convert form data", e);
            }
        }

        @Override
        public String getContentType() {
            return this.contentType.toString();
        }

        @Override
        public int getContentLength() {
            if (super.getContentLength() <= 0) {
                return super.getContentLength();
            }
            return this.contentLength;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new ServletInputStreamWrapper(this.contentData);
        }
    }

}
