package com.gitee.sop.gatewaycommon.zuul.param;

import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.manager.ParameterFormatter;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.util.RequestUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.util.RequestContentDataExtractor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author tanghc
 */
@Slf4j
public abstract class BaseParameterFormatter implements ParameterFormatter<RequestContext> {

    private FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();

    public void formatParams(ApiParam apiParam, RequestContext requestContext) throws ZuulException {
        this.format(apiParam, requestContext);
        HttpServletRequest request = requestContext.getRequest();
        String contentType = request.getContentType();
        if (StringUtils.containsIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE)) {
            byte[] bytes = apiParam.toJSONString().getBytes(StandardCharsets.UTF_8);
            requestContext.setRequest(new ChangeParamsHttpServletRequestWrapper(request, bytes));
        } else if(StringUtils.containsIgnoreCase(contentType, MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            List<String> list = Lists.newArrayList();
            try {
                for (Map.Entry<String, Object> entry : apiParam.entrySet()) {
                    list.add(entry.getKey() + "=" + URLEncoder.encode(String.valueOf(entry.getValue()), SopConstants.UTF8));
                }
            } catch (UnsupportedEncodingException e) {
                log.error("字符集不支持", e);
            }
            String paramsStr = StringUtils.join(list, "&");
            byte[] data = paramsStr.getBytes(StandardCharsets.UTF_8);
            requestContext.setRequest(new ChangeParamsHttpServletRequestWrapper(request, data));
        } else if(RequestUtil.isMultipart(request)) {
            FormHttpOutputMessage outputMessage = new FormHttpOutputMessage();
            try {
                // 转成MultipartRequest
                CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getServletContext());
                request = commonsMultipartResolver.resolveMultipart(request);
                // 重写新的值
                MultiValueMap<String, Object> builder = RequestContentDataExtractor.extract(request);
                for (Map.Entry<String, Object> entry : apiParam.entrySet()) {
                    builder.put(entry.getKey(), Collections.singletonList(String.valueOf(entry.getValue())));
                }
                MediaType mediaType = MediaType.valueOf(request.getContentType());
                // 将字段以及上传文件重写写入到流中
                formHttpMessageConverter.write(builder, mediaType, outputMessage);
                // 获取新的上传文件流
                byte[] data = outputMessage.getInput();

                requestContext.setRequest(new ChangeParamsHttpServletRequestWrapper(request, data));
                // 必须要重新指定content-type，因为此时的boundary已经发生改变
                requestContext.getZuulRequestHeaders().put("content-type", outputMessage.getHeaders().getContentType().toString());
            } catch (Exception e) {
                log.error("修改上传文件请求参数失败, apiParam:{}", apiParam, e);
            }
        } else if(HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
            Map<String, List<String>> newParams = Maps.newHashMap();
            for (Map.Entry<String, Object> entry : apiParam.entrySet()) {
                newParams.put(entry.getKey(), Collections.singletonList(String.valueOf(entry.getValue())));
            }
            requestContext.setRequestQueryParams(newParams);
        }
    }


    private static class FormHttpOutputMessage implements HttpOutputMessage {

        private HttpHeaders headers = new HttpHeaders();
        private ByteArrayOutputStream output = new ByteArrayOutputStream();

        @Override
        public HttpHeaders getHeaders() {
            return this.headers;
        }

        @Override
        public OutputStream getBody() throws IOException {
            return this.output;
        }

        public byte[] getInput() throws IOException {
            this.output.flush();
            return this.output.toByteArray();
        }

    }

    private static class ChangeParamsHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private byte[] data;

        public ChangeParamsHttpServletRequestWrapper(HttpServletRequest request, byte[] data) {
            super(request);
            this.data = data;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new ServletInputStreamWrapper(data);
        }

        @Override
        public byte[] getContentData() {
            return data;
        }

        @Override
        public int getContentLength() {
            return data.length;
        }

        @Override
        public long getContentLengthLong() {
            return data.length;
        }
    }
}
