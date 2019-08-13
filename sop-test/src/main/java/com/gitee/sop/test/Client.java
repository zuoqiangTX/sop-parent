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
 *
 * @author tanghc
 */
@Data
public class Client {
    /**
     * http请求工具
     */
    private static HttpTool httpTool = new HttpTool();

    /**
     * 请求url
     */
    private String url;

    /**
     * 平台提供的appKey
     */
    private String appId;

    /**
     * 平台提供的私钥
     */
    private String privateKey;

    /**
     * 请求成功后处理
     */
    private Callback callback;

    /**
     * 创建一个客户端
     *
     * @param url        请求url
     * @param appId      平台提供的appKey
     * @param privateKey 平台提供的私钥
     */
    public Client(String url, String appId, String privateKey) {
        this.url = url;
        this.appId = appId;
        this.privateKey = privateKey;
    }

    /**
     * 创建一个客户端
     *
     * @param url        请求url
     * @param appId      平台提供的appKey
     * @param privateKey 平台提供的私钥
     * @param callback   请求成功后处理
     */
    public Client(String url, String appId, String privateKey, Callback callback) {
        this.url = url;
        this.appId = appId;
        this.privateKey = privateKey;
        this.callback = callback;
    }

    /**
     * 发送请求
     * @param requestBuilder 请求信息
     * @return 返回结果
     */
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
        Callback call = requestBuilder.getCallback();
        if (call == null) {
            call = this.callback;
        }
        if (call != null) {
            call.callback(requestInfo, responseData);
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
        private Callback callback;

        /**
         * 设置请求url，如果指定了将会优先使用，不指定默认使用Client中的url
         *
         * @param url url
         * @return 返回RequestBuilder
         */
        public RequestBuilder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * 设置方法名
         *
         * @param method 方法名
         * @return 返回RequestBuilder
         */
        public RequestBuilder method(String method) {
            this.method = method;
            return this;
        }

        /**
         * 设置版本号
         *
         * @param version 版本号
         * @return 返回RequestBuilder
         */
        public RequestBuilder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * 设置业务参数
         *
         * @param bizContent 业务参数
         * @return 返回RequestBuilder
         */
        public RequestBuilder bizContent(Map<String, String> bizContent) {
            this.bizContent = bizContent;
            return this;
        }

        /**
         * 设置请求方法
         *
         * @param httpMethod 请求方法
         * @return 返回RequestBuilder
         */
        public RequestBuilder httpMethod(HttpTool.HTTPMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        /**
         * 设置请求头
         *
         * @param header 请求头
         * @return 返回RequestBuilder
         */
        public RequestBuilder header(Map<String, String> header) {
            this.header = header;
            return this;
        }

        /**
         * 是否忽略签名验证
         *
         * @param ignoreSign 设置true，不会构建sign字段
         * @return 返回RequestBuilder
         */
        public RequestBuilder ignoreSign(boolean ignoreSign) {
            this.ignoreSign = ignoreSign;
            return this;
        }

        /**
         * 是否是json请求
         *
         * @param postJson 设置true，请求方式变成json（application/json）
         * @return 返回RequestBuilder
         */
        public RequestBuilder postJson(boolean postJson) {
            this.postJson = postJson;
            return this;
        }

        /**
         * 设置请求成功处理
         *
         * @param callback 回调处理
         * @return 返回RequestBuilder
         */
        public RequestBuilder callback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public Callback getCallback() {
            return callback;
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
