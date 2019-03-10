package com.gitee.sop.gatewaycommon.bean;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
public class SopConstants {
    
    public static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;
    public static final String FORMAT_JSON = "json";
    public static final String DEFAULT_SIGN_METHOD = "md5";
    public static final String EMPTY_JSON = "{}";

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

    public static final int BIZ_ERROR_STATUS = 4000;

    /**
     * zookeeper存放接口路由信息的根目录
     */
    public static final String SOP_SERVICE_ROUTE_PATH = "/sop-service-route";
}
