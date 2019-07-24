package com.gitee.sop.gatewaycommon.zuul.filter;

import org.springframework.cloud.netflix.zuul.filters.pre.Servlet30WrapperFilter;

/**
 * @author tanghc
 */
public class Servlet30WrapperFilterExt extends Servlet30WrapperFilter {
    @Override
    public int filterOrder() {
        return BaseZuulFilter.SERVLET_30_WRAPPER_FILTER_ORDER;
    }

    //    @Override
//    public boolean shouldFilter() {
//        RequestContext ctx = RequestContext.getCurrentContext();
//        HttpServletRequest request = ctx.getRequest();
//        // 如果是文件上传请求，不需要包装
//        if (RequestUtil.isMultipart(request)) {
//            return false;
//        } else {
//            return super.shouldFilter();
//        }
//    }
}
