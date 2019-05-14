package com.gitee.sop.gatewaycommon.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * @author tanghc
 */
public class RequestUtil {

    private static Logger log = LoggerFactory.getLogger(RequestUtil.class);

    private RequestUtil() {
    }

    public static final String MULTIPART = "multipart/";

    private static final String UTF8 = "UTF-8";

    /**
     * 将get类型的参数转换成map，
     *
     * @param query charset=utf-8&biz_content=xxx
     * @return 返回map参数
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
    public static Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        if (paramMap == null || paramMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> retMap = new HashMap<>(paramMap.size());

        Set<Map.Entry<String, String[]>> entrySet = paramMap.entrySet();

        for (Map.Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            if (values.length >= 1) {
                retMap.put(name, values[0]);
            } else {
                retMap.put(name, "");
            }
        }
        return retMap;
    }

    /**
     * 获取文件上传表单中的字段，不包括文件，请求类型是multipart/form-data
     *
     * @param request
     * @return 返回表单中的字段内容
     */
    public static Map<String, String> convertMultipartRequestToMap(HttpServletRequest request) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //2、创建一个文件上传解析器
        ServletFileUpload upload = new ServletFileUpload(factory);
        Map<String, String> params = new HashMap<>();
        try {
            List<FileItem> fileItems = upload.parseRequest(request);
            for (FileItem fileItem : fileItems) {
                if (fileItem.isFormField()) {
                    params.put(fileItem.getFieldName(), fileItem.getString(UTF8));
                }
            }
        } catch (Exception e) {
            log.error("参数解析错误", e);
        }
        return params;
    }

    public static boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        // Don't use this filter on GET method
        if (contentType == null) {
            return false;
        }
        return contentType.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART);
    }


    public static String getText(HttpServletRequest request) throws IOException {
        return IOUtils.toString(request.getInputStream(), UTF8);
    }

}
