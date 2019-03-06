package com.gitee.sop.gatewaycommon.bean;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
public class SopConstants {
    
    public static final String RANDOM_KEY_NAME = "ssl_randomKey";
    public static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;
    public static final String UTF8 = "UTF-8";
    public static final String FORMAT_JSON = "json";
    public static final String FORMAT_XML = "xml";
    public static final String AUTHORIZATION = "Authorization";
    public static final String PREFIX_BEARER = "Bearer ";
    public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    
    public static final String DEFAULT_SIGN_METHOD = "md5";

    public static final String CONTENT_TYPE_NAME = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    
    public static final String LINE = "\n";

    public static final String EMPTY_JSON = "{}";

    public static final String SORT_DESC = "DESC";
    
    public static final String REST_PARAM_NAME = "_REST_PARAM_NAME_";
    public static final String REST_PARAM_VERSION = "_REST_PARAM_VERSION_";

    /**
     * 在拦截器中调用获取参数：
     * String cachedBody = (String)exchange.getAttribute(SopConstants.CACHE_REQUEST_BODY_OBJECT_KEY);
     */
    public static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";

    /**
     * 在拦截器中调用获取参数：
     * Map<String, String> params = exchange.getAttribute(SopConstants.CACHE_REQUEST_BODY_FOR_MAP);
     */
    public static final String CACHE_REQUEST_BODY_FOR_MAP = "cacheRequestBodyForMap";

    public static final String CACHE_API_PARAM = "cacheApiParam";

    public static final String X_BIZ_ERROR_CODE = "x-biz-error-code";

}
