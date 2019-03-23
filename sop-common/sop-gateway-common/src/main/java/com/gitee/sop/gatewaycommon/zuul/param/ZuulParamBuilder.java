package com.gitee.sop.gatewaycommon.zuul.param;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.param.ApiParamFactory;
import com.gitee.sop.gatewaycommon.param.ApiUploadContext;
import com.gitee.sop.gatewaycommon.param.ParamBuilder;
import com.gitee.sop.gatewaycommon.util.RequestUtil;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 参数解析默认实现
 *
 * @author tanghc
 */
@Slf4j
public class ZuulParamBuilder implements ParamBuilder<RequestContext> {

    private static final String CONTENT_TYPE_MULTIPART = MediaType.MULTIPART_FORM_DATA_VALUE;
    private static final String CONTENT_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;
    private static final String CONTENT_TYPE_TEXT = MediaType.TEXT_PLAIN_VALUE;
    private static final String GET = "get";

    @Override
    public ApiParam build(RequestContext ctx) {
        HttpServletRequest request = ctx.getRequest();
        // zuul会做一层包装
        if (request instanceof HttpServletRequestWrapper) {
            HttpServletRequestWrapper req = (HttpServletRequestWrapper) request;
            request = req.getRequest();
        }
        Map<String, Object> params = this.getParams(request);
        ApiParam apiParams = ApiParamFactory.build(params);
        ApiUploadContext apiUploadContext = this.buildApiUploadContext(request);
        apiParams.setApiUploadContext(apiUploadContext);
        return apiParams;
    }

    public Map<String, Object> getParams(HttpServletRequest request) {
        Map<String, Object> params = null;

        if (GET.equalsIgnoreCase(request.getMethod())) {
            params = RequestUtil.convertRequestParamsToMap(request);
        } else {
            String contentType = request.getContentType();

            if (contentType == null) {
                contentType = "";
            }

            contentType = contentType.toLowerCase();

            // json或者纯文本形式
            if (contentType.contains(CONTENT_TYPE_JSON) || contentType.contains(CONTENT_TYPE_TEXT)) {
                String txt = SopConstants.EMPTY_JSON;
                try {
                    txt = RequestUtil.getText(request);
                } catch (Exception e) {
                    log.error("获取纯文本内容失败", e);
                }
                params = JSON.parseObject(txt);
            } else {
                params = RequestUtil.convertRequestParamsToMap(request);
            }
        }

        return params;
    }

    protected ApiUploadContext buildApiUploadContext(HttpServletRequest request) {
        ApiUploadContext apiUploadContext = null;
        String contectType = request.getContentType();
        if (contectType.contains(CONTENT_TYPE_MULTIPART)) {
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                    request.getSession().getServletContext());
            // 检查form中是否有enctype="multipart/form-data"
            if (multipartResolver.isMultipart(request)) {
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                Map<String, MultipartFile> fileMap = multiRequest.getFileMap();
                Map<String, MultipartFile> finalMap = new HashMap<>(fileMap.size());

                Set<String> keys = fileMap.keySet();
                for (String name : keys) {
                    MultipartFile file = fileMap.get(name);
                    if (file.getSize() > 0) {
                        finalMap.put(name, file);
                    }
                }
                if (finalMap.size() > 0) {
                    // 保存上传文件
                    apiUploadContext = new ApiUploadContext(finalMap);
                }
            }
        }
        return apiUploadContext;
    }

}
