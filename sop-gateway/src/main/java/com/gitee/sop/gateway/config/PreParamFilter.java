package com.gitee.sop.gateway.config;

import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.param.ParamNames;
import com.gitee.sop.gatewaycommon.zuul.ZuulContext;
import com.gitee.sop.gatewaycommon.zuul.filter.BaseZuulFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpMethod;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author tanghc
 */
public class PreParamFilter extends BaseZuulFilter {
    @Override
    protected FilterType getFilterType() {
        return FilterType.PRE;
    }

    @Override
    protected int getFilterOrder() {
        return 0;
    }

    @Override
    protected Object doRun(RequestContext requestContext) throws ZuulException {
        ApiParam apiParam = ZuulContext.getApiParam();
        JSONObject bizContent = apiParam.getJSONObject(ParamNames.BIZ_CONTENT_NAME);
        // 修改biz_content中的参数
        bizContent.put("name", "修改了111");
        apiParam.put(ParamNames.BIZ_CONTENT_NAME, bizContent.toJSONString());

        HttpServletRequest request = requestContext.getRequest();
        String contentType = request.getContentType();
        if (StringUtils.containsIgnoreCase(contentType, "json")) {
            byte[] bytes = apiParam.toJSONString().getBytes(StandardCharsets.UTF_8);
            requestContext.setRequest(new HttpServletRequestWrapper(requestContext.getRequest()) {
                @Override
                public ServletInputStream getInputStream() throws IOException {
                    return new ServletInputStreamWrapper(bytes);
                }

                @Override
                public byte[] getContentData() {
                    return bytes;
                }

                @Override
                public int getContentLength() {
                    return bytes.length;
                }

                @Override
                public long getContentLengthLong() {
                    return bytes.length;
                }
            });
        } else if(StringUtils.containsIgnoreCase(contentType, "form")) {
            List<String> list = Lists.newArrayList();
            try {
                for (Map.Entry<String, Object> entry : apiParam.entrySet()) {
                    list.add(entry.getKey() + "=" + URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String paramsStr = StringUtils.join(list, "&");
            byte[] bytes = paramsStr.getBytes(StandardCharsets.UTF_8);
            requestContext.setRequest(new HttpServletRequestWrapper(requestContext.getRequest()) {
                @Override
                public ServletInputStream getInputStream() throws IOException {
                    return new ServletInputStreamWrapper(bytes);
                }

                @Override
                public byte[] getContentData() {
                    return bytes;
                }

                @Override
                public int getContentLength() {
                    return bytes.length;
                }

                @Override
                public long getContentLengthLong() {
                    return bytes.length;
                }
            });
        } else if(HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
            Map<String, List<String>> newParams = Maps.newHashMap();
            for (Map.Entry<String, Object> entry : apiParam.entrySet()) {
                newParams.put(entry.getKey(), Collections.singletonList(String.valueOf(entry.getValue())));
            }
            requestContext.setRequestQueryParams(newParams);
        }

        return null;
    }
}
