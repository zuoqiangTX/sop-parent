package com.gitee.sop.sdk.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.sdk.common.OpenConfig;
import com.gitee.sop.sdk.common.RequestForm;
import com.gitee.sop.sdk.sign.SopSignException;
import com.gitee.sop.sdk.request.BaseRequest;
import com.gitee.sop.sdk.response.BaseResponse;
import com.gitee.sop.sdk.exception.SdkException;
import com.gitee.sop.sdk.sign.SopSignature;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.Map;

/**
 * 请求客户端
 *
 * @author tanghc
 */
public class OpenClient {
    private static final Log log = LogFactory.getLog(OpenClient.class);

    private static final OpenConfig DEFAULT_CONFIG = new OpenConfig();

    private static final char DOT = '.';
    private static final char UNDERLINE = '_';
    public static final String GATEWAY_CODE_NAME = "code";
    public static final String GATEWAY_MSG_NAME = "msg";
    public static final String DATA_SUFFIX = "_response";

    private String url;
    private String appKey;
    private String privateKey;

    private OpenConfig openConfig;
    private OpenRequest openRequest;

    public OpenClient(String url, String appKey, String privateKey) {
        this(url, appKey, privateKey, DEFAULT_CONFIG);
    }

    public OpenClient(String url, String appKey, String privateKey, OpenConfig openConfig) {
        if (openConfig == null) {
            throw new IllegalArgumentException("openConfig不能为null");
        }
        this.url = url;
        this.appKey = appKey;
        this.privateKey = privateKey;
        this.openConfig = openConfig;

        this.openRequest = new OpenRequest(openConfig);
    }

    /**
     * 请求接口
     *
     * @param request 请求对象
     * @param <T>     返回对应的Response
     * @return 返回Response
     */
    public <T extends BaseResponse> T execute(BaseRequest<T> request) {
        return this.execute(request, null);
    }

    /**
     * 请求接口
     *
     * @param request     请求对象
     * @param accessToken jwt
     * @param <T>         返回对应的Response
     * @return 返回Response
     */
    public <T extends BaseResponse> T execute(BaseRequest<T> request, String accessToken) {
        RequestForm requestForm = request.createRequestForm();
        // 表单数据
        Map<String, String> form = requestForm.getForm();
        if (accessToken != null) {
            form.put(this.openConfig.getAccessTokenName(), accessToken);
        }
        form.put(this.openConfig.getAppKeyName(), this.appKey);

        String content = SopSignature.getSignContent(form);
        String sign = null;
        try {
            sign = SopSignature.rsa256Sign(content, privateKey, "utf-8");
        } catch (SopSignException e) {
            throw new SdkException("构建签名错误", e);
        }

        form.put(this.openConfig.getSignName(), sign);

        String resp = doExecute(this.url, requestForm, Collections.emptyMap());
        if (log.isDebugEnabled()) {
            log.debug("----------- 请求信息 -----------"
                    + "\n请求参数：" + SopSignature.getSignContent(form)
                    + "\n待签名内容：" + content
                    + "\n签名(sign)：" + sign
                    + "\n----------- 返回结果 -----------"
                    + "\n" + resp
            );
        }
        return this.parseResponse(resp, request);
    }

    protected String doExecute(String url, RequestForm requestForm, Map<String, String> header) {
        return openRequest.request(this.url, requestForm, header);
    }

    protected <T extends BaseResponse> T parseResponse(String resp, BaseRequest<T> request) {
        String method = request.getMethod();
        String dataName = method.replace(DOT, UNDERLINE) + DATA_SUFFIX;
        JSONObject jsonObject = JSON.parseObject(resp);
        JSONObject data = jsonObject.getJSONObject(dataName);
        T t = data.toJavaObject(request.getResponseClass());
        t.setBody(data.toJSONString());
        return t;
    }


}
