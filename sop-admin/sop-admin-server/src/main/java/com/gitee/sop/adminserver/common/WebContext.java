package com.gitee.sop.adminserver.common;

import com.gitee.easyopen.ApiContext;
import com.gitee.sop.adminserver.entity.AdminUserInfo;

import javax.servlet.http.HttpSession;

public class WebContext {
    private static WebContext INSTANCE = new WebContext();

    private static final String S_USER = "s_user";

    private WebContext() {
    }

    public static WebContext getInstance() {
        return INSTANCE;
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    public AdminUserInfo getLoginUser() {
        HttpSession session = ApiContext.getManagedSession();
        if (session == null) {
            return null;
        }
        return (AdminUserInfo) session.getAttribute(S_USER);
    }

    public void setLoginUser(HttpSession session, AdminUserInfo user) {
        if (session != null) {
            session.setAttribute(S_USER, user);
        }
    }


}
