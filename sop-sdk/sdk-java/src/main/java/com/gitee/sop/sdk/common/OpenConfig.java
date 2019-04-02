package com.gitee.sop.sdk.common;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class OpenConfig {
    /** 成功返回码值 */
    private String successCode = SdkConfig.SUCCESS_CODE;
    /** 默认版本号 */
    private String defaultVersion = SdkConfig.DEFAULT_VERSION;
    /** 接口属性名 */
    private String apiName = "method";
    /** 版本号名称 */
    private String versionName = "version";
    /** appKey名称 */
    private String appKeyName = "app_id";
    /** data名称 */
    private String dataName = "biz_content";
    /** 时间戳名称 */
    private String timestampName = "timestamp";
    /** 时间戳格式 */
    private String timestampPattern = "yyyy-MM-dd HH:mm:ss";
    /** 签名串名称 */
    private String signName = "sign";
    /** 格式化名称 */
    private String formatName = "format";
    /** 格式类型 */
    private String formatType = "json";
    /** accessToken名称 */
    private String accessTokenName = "app_auth_token";
    /** 国际化语言 */
    private String locale = "zh-CN";
    /** 响应code名称 */
    private String responseCodeName = "code";
    /** 请求超时时间 */
    private int connectTimeoutSeconds = 10;
    /** http读取超时时间 */
    private int readTimeoutSeconds = 10;
}
