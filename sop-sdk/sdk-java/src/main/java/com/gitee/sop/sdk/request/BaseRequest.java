package com.gitee.sop.sdk.request;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.sdk.common.RequestForm;
import com.gitee.sop.sdk.common.SdkConfig;
import com.gitee.sop.sdk.common.UploadFile;
import com.gitee.sop.sdk.response.BaseResponse;
import com.gitee.sop.sdk.util.ClassUtil;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求对象父类，后续请求对象都要继承这个类
 * <p>
 * 参数	            类型	    是否必填	    最大长度	    描述	            示例值
 * app_id	        String	是	        32	    支付宝分配给开发者的应用ID	2014072300007148
 * method	        String	是	        128	    接口名称	alipay.trade.fastpay.refund.query
 * format	        String	否	        40	    仅支持JSON	JSON
 * charset	    String	是	        10	    请求使用的编码格式，如utf-8,gbk,gb2312等	utf-8
 * sign_type	    String	是	        10	    商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2	RSA2
 * sign	        String	是	        344	    商户请求参数的签名串，详见签名	详见示例
 * timestamp	    String	是	        19	    发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"	2014-07-24 03:07:50
 * version	        String	是	        3	    调用的接口版本，固定为：1.0	1.0
 * app_auth_token	String	否	        40	    详见应用授权概述
 * biz_content	    String	是		请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递，具体参照各产品快速接入文档
 *
 * @param <T> 对应的Response对象
 */
public abstract class BaseRequest<T extends BaseResponse> {

    private String method;
    private String format = SdkConfig.FORMAT_TYPE;
    private String charset = SdkConfig.CHARSET;
    private String signType = SdkConfig.SIGN_TYPE;
    private String timestamp = new SimpleDateFormat(SdkConfig.TIMESTAMP_PATTERN).format(new Date());
    private String version;

    private String bizContent;
    private Object bizModel;

    /**
     * 上传文件
     */
    private List<UploadFile> files;

    private Class<T> responseClass;

    protected abstract String method();

    @SuppressWarnings("unchecked")
    public BaseRequest() {
        this.method = this.method();
        this.version = this.version();

        this.responseClass = (Class<T>) ClassUtil.getSuperClassGenricType(this.getClass(), 0);
    }

    protected String version() {
        return SdkConfig.DEFAULT_VERSION;
    }

    public RequestForm createRequestForm() {
        // 公共请求参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", this.method);
        params.put("format", this.format);
        params.put("charset", this.charset);
        params.put("sign_type", this.signType);
        params.put("timestamp", this.timestamp);
        params.put("version", this.version);

        // 业务参数
        String biz_content = buildBizContent();

        params.put("biz_content", biz_content);

        RequestForm requestForm = new RequestForm(params);
        requestForm.setFiles(this.files);
        return requestForm;
    }

    protected String buildBizContent() {
        if (bizModel != null) {
            return JSON.toJSONString(bizModel);
        } else {
            return this.bizContent;
        }
    }

    public String getMethod() {
        return method;
    }

    protected void setMethod(String method) {
        this.method = method;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setBizContent(String bizContent) {
        this.bizContent = bizContent;
    }

    public void setBizModel(Object bizModel) {
        this.bizModel = bizModel;
    }

    public void setFiles(List<UploadFile> files) {
        this.files = files;
    }

    public Class<T> getResponseClass() {
        return responseClass;
    }
}
