package com.gitee.sop.gatewaycommon.bean;

import com.gitee.sop.gatewaycommon.param.UploadContext;
import com.netflix.zuul.context.RequestContext;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.session.SessionManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * 应用上下文,方便获取信息
 * 
 * @author tanghc
 *
 */
public class ApiContext {
    private static final String ATTR_PARAM = "zuul.common.api.param";
    private static final String ATTR_UPLOAD_CONTEXT = "zuul.common.api.upload_context";

    private ApiContext(){}

    private static void setAttr(String name, Object val) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            request.setAttribute(name, val);
        }
    }

    private static Object getAttr(String name) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return request.getAttribute(name);
    }

    /**
     * 获取随机码
     * @return 返回随机码
     */
    public static String getRandomKey() {
        HttpSession session = getSession();
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(SopConstants.RANDOM_KEY_NAME);
    }



    /**
     * 获取HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return RequestContext.getCurrentContext().getRequest();
    }

    /**
     * 返回默认的HttpServletRequest.getSession();
     *
     * @return 没有返回null
     */
    public static HttpSession getSession() {
        HttpServletRequest req = getRequest();
        if (req == null) {
            return null;
        } else {
            return req.getSession();
        }
    }

    /**
     * 获取session管理器
     * @return 返回SessionManager
     */
    public static SessionManager getSessionManager() {
        return getApiConfig().getSessionManager();
    }

    /**
     * 返回自定义的session,被SessionManager管理
     *
     * @return 如果sessionId为null，则返回null
     */
    public static HttpSession getManagedSession() {
        String sessionId = getSessionId();
        if (sessionId != null) {
            return getSessionManager().getSession(sessionId);
        } else {
            return null;
        }
    }

    /**
     * 同getSessionId()
     * @return 返回accessToken,没有返回null
     */
    public static String getAccessToken() {
        return getSessionId();
    }


    /**
     * 获取登陆的token
     *
     * @return 没有返回null
     */
    public static String getSessionId() {
        ApiParam apiParam = getApiParam();
        if (apiParam == null) {
            return null;
        }
        return apiParam.fetchAccessToken();
    }

    /**
     * 获取本地化，从HttpServletRequest中获取，没有则返回Locale.SIMPLIFIED_CHINESE
     *
     * @return Locale
     */
    public static Locale getLocale() {
        HttpServletRequest req = getRequest();
        if (req == null) {
            return Locale.SIMPLIFIED_CHINESE;
        }
        return req.getLocale();
    }

    public static void setApiParam(ApiParam apiParam) {
        setAttr(ATTR_PARAM, apiParam);
    }

    /**
     * 获取系统参数
     *
     * @return 返回ApiParam
     */
    public static ApiParam getApiParam() {
        return (ApiParam) getAttr(ATTR_PARAM);
    }

    public static ApiConfig getApiConfig() {
        return ApiConfig.getInstance();
    }

    public static void setApiConfig(ApiConfig apiConfig) {
        ApiConfig.setInstance(apiConfig);
    }


    public static ServletContext getServletContext() {
        ServletContext ctx = null;
        HttpSession session = getSession();
        if (session != null) {
            ctx = session.getServletContext();
        }
        return ctx;
    }

    /**
     * 获取上传文件，如果客户端有文件上传，从这里取。
     * @return 如果没有文件上传，返回null
     */
    public static UploadContext getUploadContext() {
        return (UploadContext) getAttr(ATTR_UPLOAD_CONTEXT);
    }

    public static void setUploadContext(UploadContext uploadCtx) {
        setAttr(ATTR_UPLOAD_CONTEXT, uploadCtx);
    }

    /**
     * 获取response
     * @return 返回response
     */
    public static HttpServletResponse getResponse() {
        return RequestContext.getCurrentContext().getResponse();
    }

}
