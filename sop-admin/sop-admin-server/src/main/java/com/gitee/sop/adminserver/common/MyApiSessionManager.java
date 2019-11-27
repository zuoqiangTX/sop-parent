package com.gitee.sop.adminserver.common;

import com.gitee.easyopen.exception.ApiException;
import com.gitee.easyopen.session.ApiSessionManager;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * @author tanghc
 */
@Slf4j
public class MyApiSessionManager extends ApiSessionManager {

    private volatile LoadingCache<String, HttpSession> cache;

    @Override
    public HttpSession getSession(String sessionId) {
        if (sessionId == null) {
            return this.createSession(sessionId);
        }
        try {
            return getCache().get(sessionId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiException("create session error");
        }
    }

    /**
     * 创建一个session
     *
     * @param sessionId 传null将返回一个新session
     * @return 返回session
     */
    @Override
    protected HttpSession createSession(String sessionId) {
        ServletContext servletContext = getServletContext();
        HttpSession session = this.newSession(sessionId, servletContext);
        session.setMaxInactiveInterval(getSessionTimeout());
        getCache().put(session.getId(), session);
        return session;
    }

    public LoadingCache<String, HttpSession> getCache() {
        if (cache == null) {
            synchronized (ApiSessionManager.class) {
                if (cache == null) {
                    cache = buildCache();
                }
            }
        }
        return cache;
    }

}
