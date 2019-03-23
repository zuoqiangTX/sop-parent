package com.gitee.sop.gatewaycommon.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author tanghc
 */
public class RequestUtil {

    private RequestUtil(){}

    private static final String UTF8 = "UTF-8";

    /**
     * 将get类型的参数转换成map，
     *
     * @param query charset=utf-8&biz_content=xxx
     * @return
     */
    public static Map<String, String> parseQueryToMap(String query) {
        if (query == null) {
            return Collections.emptyMap();
        }
        String[] queryList = StringUtils.split(query, '&');
        Map<String, String> params = new HashMap<>(16);
        for (String param : queryList) {
            String[] paramArr = param.split("\\=");
            if (paramArr.length == 2) {
                try {
                    params.put(paramArr[0], URLDecoder.decode(paramArr[1], UTF8));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            } else if (paramArr.length == 1) {
                params.put(paramArr[0], "");
            }
        }
        return params;
    }

    /**
     * request中的参数转换成map
     *
     * @param request request对象
     * @return 返回参数键值对
     */
    public static Map<String, Object> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        if(paramMap == null || paramMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> retMap = new HashMap<>(paramMap.size());

        Set<Map.Entry<String, String[]>> entrySet = paramMap.entrySet();

        for (Map.Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            if (values.length == 1) {
                retMap.put(name, values[0]);
            } else if (values.length > 1) {
                retMap.put(name, values);
            } else {
                retMap.put(name, "");
            }
        }

        return retMap;
    }

    public static String getText(HttpServletRequest request) throws IOException {
        return IOUtils.toString(request.getInputStream(), UTF8);
    }

}
