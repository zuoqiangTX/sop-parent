package com.gitee.sop.sdk.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.sdk.common.DataNameBuilder;
import com.gitee.sop.sdk.common.OpenConfig;
import com.gitee.sop.sdk.common.RequestForm;
import com.gitee.sop.sdk.common.SopSdkConstants;
import com.gitee.sop.sdk.common.SopSdkErrors;
import com.gitee.sop.sdk.exception.SdkException;
import com.gitee.sop.sdk.request.BaseRequest;
import com.gitee.sop.sdk.response.BaseResponse;
import com.gitee.sop.sdk.response.ErrorResponse;
import com.gitee.sop.sdk.sign.SopSignException;
import com.gitee.sop.sdk.sign.SopSignature;
import com.gitee.sop.sdk.sign.StringUtils;
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

    private String url;
    private String appId;
    private String privateKey;
    /**
     * 开放平台提供的公钥
     */
    private String publicKeyPlatform;

    private OpenConfig openConfig;
    private OpenRequest openRequest;
    private DataNameBuilder dataNameBuilder;

    public OpenClient(String url, String appId, String privateKeyIsv) {
        this(url, appId, privateKeyIsv, DEFAULT_CONFIG);
    }

    public OpenClient(String url, String appId, String privateKeyIsv, String publicKeyPlatform) {
        this(url, appId, privateKeyIsv);
        this.publicKeyPlatform = publicKeyPlatform;
    }

    public OpenClient(String url, String appId, String privateKeyIsv, OpenConfig openConfig) {
        if (openConfig == null) {
            throw new IllegalArgumentException("openConfig不能为null");
        }
        this.url = url;
        this.appId = appId;
        this.privateKey = privateKeyIsv;
        this.openConfig = openConfig;

        this.openRequest = new OpenRequest(openConfig);
        this.dataNameBuilder = openConfig.getDataNameBuilder();
    }

    public OpenClient(String url, String appId, String privateKeyIsv, String publicKeyPlatform, OpenConfig openConfig) {
        this(url, appId, privateKeyIsv, openConfig);
        this.publicKeyPlatform = publicKeyPlatform;
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
        RequestForm requestForm = request.createRequestForm(this.openConfig);
        // 表单数据
        Map<String, String> form = requestForm.getForm();
        if (accessToken != null) {
            form.put(this.openConfig.getAccessTokenName(), accessToken);
        }
        form.put(this.openConfig.getAppKeyName(), this.appId);

        String content = SopSignature.getSignContent(form);
        String sign = null;
        try {
            sign = SopSignature.rsaSign(content, privateKey, openConfig.getCharset(), openConfig.getSignType());
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
        return openRequest.request(url, requestForm, header);
    }

    protected <T extends BaseResponse> T parseResponse(String resp, BaseRequest<T> request) {
        String method = request.getMethod();
        String rootNodeName = dataNameBuilder.build(method);
        JSONObject jsonObject = JSON.parseObject(resp);
        String errorResponseName = this.openConfig.getErrorResponseName();
        boolean errorResponse = jsonObject.containsKey(errorResponseName);
        if (errorResponse) {
            rootNodeName = errorResponseName;
        }
        JSONObject data = jsonObject.getJSONObject(rootNodeName);
        String sign = jsonObject.getString(openConfig.getSignName());
        // 是否要验证返回的sign
        if (StringUtils.areNotEmpty(sign, publicKeyPlatform)) {
            String signContent = buildBizJson(rootNodeName, resp);
            if (!this.checkResponseSign(signContent, sign, publicKeyPlatform)) {
                ErrorResponse error = SopSdkErrors.CHECK_RESPONSE_SIGN_ERROR.getErrorResponse();
                data = JSON.parseObject(JSON.toJSONString(error));
            }
        }
        T t = data.toJavaObject(request.getResponseClass());
        t.setBody(data.toJSONString());
        return t;
    }

    protected String buildBizJson(String rootNodeName, String body) {
        int indexOfRootNode = body.indexOf(rootNodeName);
        if (indexOfRootNode < 0) {
            rootNodeName = SopSdkConstants.ERROR_RESPONSE_KEY;
            indexOfRootNode = body.indexOf(rootNodeName);
        }
        String result = null;
        if (indexOfRootNode > 0) {
            result = buildJsonNodeData(body, rootNodeName, indexOfRootNode);
        }
        return result;
    }

    protected String buildJsonNodeData(String body, String rootNodeName, int indexOfRootNode) {
        int signDataStartIndex = indexOfRootNode + rootNodeName.length() + 2;
        int indexOfSign = body.indexOf("\"" + openConfig.getSignName() + "\"");
        if (indexOfSign < 0) {
            return null;
        }
        int length = indexOfSign - 1;
        return body.substring(signDataStartIndex, length);
    }

    protected <T extends BaseResponse> boolean checkResponseSign(String signContent, String sign, String publicKeyPlatform) {
        try {
            String charset = this.openConfig.getCharset();
            String signType = this.openConfig.getSignType();
            return SopSignature.rsaCheck(signContent, sign, publicKeyPlatform, charset, signType);
        } catch (SopSignException e) {
            log.error("验证服务端sign出错，signContent：" + signContent, e);
            return false;
        }
    }


}
