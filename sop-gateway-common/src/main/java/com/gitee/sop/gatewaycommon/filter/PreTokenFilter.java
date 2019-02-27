package com.gitee.sop.gatewaycommon.filter;

import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tanghc
 */
public class PreTokenFilter extends BaseZuulFilter {

    @Override
    protected FilterType getFilterType() {
        return FilterType.PRE;
    }

    @Override
    protected int getFilterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    protected Object doRun(RequestContext ctx) throws ZuulException {
        Object serviceId = ctx.get(FilterConstants.SERVICE_ID_KEY);
        log.info("serviceId:{}", serviceId);
        HttpServletRequest request = ctx.getRequest();

        log.info("send {} request to {}", request.getMethod(), request.getRequestURL().toString());

        String accessToken = request.getParameter("access_token");
        if (StringUtils.isBlank(accessToken)) {
            throw ErrorEnum.AOP_INVALID_APP_AUTH_TOKEN.getErrorMeta().getException();
        }
        return null;
    }

}
