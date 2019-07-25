package com.gitee.sop.servercommon.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author tanghc
 */
@Slf4j
public class OpenUtil {

    private static final String CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT = "text/plain";

    private static final String UTF8 = "UTF-8";

    /**
     * 从request中获取参数。如果提交方式是application/x-www-form-urlencoded，则组装成json格式。
     *
     * @param request request对象
     * @return 返回json
     * @throws IOException
     */
    public static JSONObject getRequestParams(HttpServletRequest request) {
        String requestJson = "{}";
        String contentType = request.getContentType();
        if (StringUtils.isBlank(contentType)) {
            return new JSONObject();
        }
        contentType = contentType.toLowerCase();
        if (StringUtils.containsAny(contentType, CONTENT_TYPE_JSON, CONTENT_TYPE_TEXT)) {
            try {
                requestJson = IOUtils.toString(request.getInputStream(), UTF8);
            } catch (IOException e) {
                log.error("获取文本数据流失败", e);
            }
        } else if (StringUtils.containsAny(contentType, CONTENT_TYPE_URLENCODED)) {
            Map<String, Object> params = convertRequestParamsToMap(request);
            requestJson = JSON.toJSONString(params);
        }
        return JSON.parseObject(requestJson);
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
        Map<String, Object> retMap = new HashMap<String, Object>(paramMap.size());

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

}
