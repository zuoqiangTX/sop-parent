package com.gitee.sop.adminserver.interceptor;

import com.gitee.easyopen.ApiContext;
import com.gitee.easyopen.ApiMeta;
import com.gitee.easyopen.ParamNames;
import com.gitee.easyopen.interceptor.ApiInterceptorAdapter;
import com.gitee.sop.adminserver.common.AdminErrors;
import com.gitee.sop.adminserver.common.WebContext;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器，验证用户是否登录
 *
 * @author tanghc
 */
public class LoginInterceptor extends ApiInterceptorAdapter {

    public static final String HEADER_TOKEN_NAME = ParamNames.ACCESS_TOKEN_NAME;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu)
            throws Exception {
        String accessToken = request.getHeader(HEADER_TOKEN_NAME);
        if (StringUtils.isNotBlank(accessToken)) {
            ApiContext.getApiParam().put(ParamNames.ACCESS_TOKEN_NAME, accessToken);
        }
        if (WebContext.getInstance().getLoginUser() != null) {
            return true;
        } else {
            throw AdminErrors.NO_LOGIN.getException();
        }
    }

    @Override
    public boolean match(ApiMeta apiMeta) {
        String name = apiMeta.getName();
        if (name.startsWith("nologin.")) { // 以‘nologin.’开头的接口不拦截
            return false;
        } else {
            return true;
        }
    }

}
