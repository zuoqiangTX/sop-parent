package com.gitee.sop.gatewaycommon.zuul.filter;

import com.gitee.sop.gatewaycommon.util.RequestUtil;
import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.pre.Servlet30WrapperFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tanghc
 */
public class Servlet30WrapperFilterExt extends Servlet30WrapperFilter {
    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        // 不是上传文件请求，则进行包装
        return !RequestUtil.isMultipart(request);
    }
}
