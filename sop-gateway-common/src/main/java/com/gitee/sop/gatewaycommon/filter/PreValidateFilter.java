package com.gitee.sop.gatewaycommon.filter;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.exception.ApiException;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.validate.Validator;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * 前置校验
 * @author tanghc
 */
public class PreValidateFilter extends BaseZuulFilter {
    @Override
    protected FilterType getFilterType() {
        return FilterType.PRE;
    }

    @Override
    protected int getFilterOrder() {
        // 在org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter前面
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    protected Object doRun(RequestContext requestContext) throws ZuulException {
        HttpServletRequest request = requestContext.getRequest();
        ApiConfig apiConfig = ApiContext.getApiConfig();
        // 解析参数
        ApiParam param = apiConfig.getParamParser().parse(request);
        ApiContext.setApiParam(param);
        // 验证操作，这里有负责验证签名参数
        Validator validator = apiConfig.getValidator();
        try {
            validator.validate(param);
        } catch (ApiException e) {
            log.error("验证失败，params:{}", param.toJSONString(), e);
            throw e;
        }
        return null;
    }

}
