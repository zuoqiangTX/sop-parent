package com.gitee.sop.gatewaycommon.zuul.param;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.param.ApiParamFactory;
import com.gitee.sop.gatewaycommon.param.ApiUploadContext;
import com.gitee.sop.gatewaycommon.param.ParamBuilder;
import com.gitee.sop.gatewaycommon.util.RequestUtil;
import com.gitee.sop.gatewaycommon.zuul.ZuulContext;
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
        Map<String, Object> params = this.getParams(request);
        return ApiParamFactory.build(params);
    }

    public Map<String, Object> getParams(HttpServletRequest request) {
        // zuul会做一层包装
        if (request instanceof HttpServletRequestWrapper) {
            HttpServletRequestWrapper req = (HttpServletRequestWrapper) request;
            request = req.getRequest();
        }
        Map<String, Object> params = null;

        if (GET.equalsIgnoreCase(request.getMethod())) {
            params = RequestUtil.convertRequestParamsToMap(request);
        } else {
            String contectType = request.getContentType();

            if (contectType == null) {
                contectType = "";
            }

            contectType = contectType.toLowerCase();

            // json或者纯文本形式
            if (contectType.contains(CONTENT_TYPE_JSON) || contectType.contains(CONTENT_TYPE_TEXT)) {
                String txt = SopConstants.EMPTY_JSON;
                try {
                    txt = RequestUtil.getText(request);
                } catch (Exception e) {
                    log.error("获取纯文本内容失败", e);
                }
                params = JSON.parseObject(txt);
            } else if (contectType.contains(CONTENT_TYPE_MULTIPART)) {
                // 上传文件形式
                params = this.parseUploadRequest(request);
            } else {
                params = RequestUtil.convertRequestParamsToMap(request);
            }
        }

        return params;
    }

    /**
     * 解析文件上传请求
     *
     * @param request
     * @return 返回json字符串
     */
    protected Map<String, Object> parseUploadRequest(HttpServletRequest request) {
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
                ZuulContext.setUploadContext(new ApiUploadContext(finalMap));
            }
        }

        return RequestUtil.convertRequestParamsToMap(request);
    }


}