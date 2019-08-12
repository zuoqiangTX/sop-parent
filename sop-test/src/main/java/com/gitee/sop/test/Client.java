package com.gitee.sop.test;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.test.alipay.AlipayApiException;
import com.gitee.sop.test.alipay.AlipaySignature;
import lombok.Data;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 简易客户端
 * @author tanghc
 */
@Data
public class Client {
    private static HttpTool httpTool = new HttpTool();

    private String url;
    private String appId;
    private String privateKey;

    private Callback callback;

    public Client(String url, String appId, String privateKey) {
        this.url = url;
        this.appId = appId;
        this.privateKey = privateKey;
    }

    public Client(String url, String appId, String privateKey, Callback callback) {
        this.url = url;
        this.appId = appId;
        this.privateKey = privateKey;
        this.callback = callback;
    }

    public String execute(RequestBuilder requestBuilder) {
        RequestInfo requestInfo = requestBuilder.build(appId, privateKey);
        HttpTool.HTTPMethod httpMethod = requestInfo.getHttpMethod();
        boolean postJson = requestInfo.isPostJson();
        Map<String, ?> form = requestInfo.getForm();
        Map<String, String> header = requestInfo.getHeader();
        String requestUrl = requestInfo.getUrl() != null ? requestInfo.getUrl() : url;
        String responseData = null;
        try {
            // 发送请求
            if (httpMethod == HttpTool.HTTPMethod.POST && postJson) {
                responseData = httpTool.requestJson(requestUrl, JSON.toJSONString(form), header);
            } else {
                responseData = httpTool.request(requestUrl, form, header, httpMethod);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (callback != null) {
            callback.callback(requestInfo, responseData);
        }
        return responseData;
    }

    public interface Callback {
        void callback(RequestInfo requestInfo, String responseData);
    }

    public static class RequestBuilder {
        private static final String DEFAULT_VERSION = "1.0";

        private String url;
        private String method;
        private String version = DEFAULT_VERSION;
        private Map<String, String> bizContent;
        private HttpTool.HTTPMethod httpMethod;
        private Map<String, String> header;
        private boolean ignoreSign;
        private boolean postJson;

        public RequestBuilder url(String url) {
            this.url = url;
            return this;
        }

        public RequestBuilder method(String method) {
            this.method = method;
            return this;
        }

        public RequestBuilder version(String version) {
            this.version = version;
            return this;
        }

        public RequestBuilder bizContent(Map<String, String> bizContent) {
            this.bizContent = bizContent;
            return this;
        }

        public RequestBuilder httpMethod(HttpTool.HTTPMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public RequestBuilder header(Map<String, String> header) {
            this.header = header;
            return this;
        }

        public RequestBuilder ignoreSign(boolean ignoreSign) {
            this.ignoreSign = ignoreSign;
            return this;
        }

        public RequestBuilder postJson(boolean postJson) {
            this.postJson = postJson;
            return this;
        }


        public RequestInfo build(String appId, String privateKey) {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", appId);
            if (method != null) {
                params.put("method", method);
            }
            if (version != null) {
                params.put("version", version);
            }
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            // 业务参数
            params.put("biz_content", JSON.toJSONString(bizContent == null ? Collections.emptyMap() : bizContent));

            if (!ignoreSign) {
                String content = AlipaySignature.getSignContent(params);
                String sign = null;
                try {
                    sign = AlipaySignature.rsa256Sign(content, privateKey, "utf-8");
                } catch (AlipayApiException e) {
                    throw new RuntimeException(e);
                }
                params.put("sign", sign);
            }

            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setUrl(url);
            requestInfo.setMethod(method);
            requestInfo.setVersion(version);
            requestInfo.setForm(params);
            requestInfo.setHeader(header);
            requestInfo.setPostJson(postJson);
            requestInfo.setHttpMethod(httpMethod);
            return requestInfo;
        }
    }

    @Data
    public static class RequestInfo {
        private String url;
        private String method;
        private String version;
        private boolean postJson;
        private Map<String, ?> form;
        private Map<String, String> header;
        private HttpTool.HTTPMethod httpMethod;
    }

}
