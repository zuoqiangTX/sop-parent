package com.gitee.sop.servercommon.bean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceContext extends ConcurrentHashMap<String, Object> {

    public static final String REQUEST_KEY = "request";
    public static final String RESPONSE_KEY = "response";
    protected static Class<? extends ServiceContext> contextClass = ServiceContext.class;

    protected static final ThreadLocal<? extends ServiceContext> threadLocal = new ThreadLocal<ServiceContext>() {
        @Override
        protected ServiceContext initialValue() {
            try {
                return contextClass.newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    };


    public ServiceContext() {
        super();
    }

    public Locale getLocale() {
        return getRequest().getLocale();
    }

    /**
     * Override the default ServiceContext
     *
     * @param clazz
     */
    public static void setContextClass(Class<? extends ServiceContext> clazz) {
        contextClass = clazz;
    }


    /**
     * Get the current ServiceContext
     *
     * @return the current ServiceContext
     */
    public static ServiceContext getCurrentContext() {
        return threadLocal.get();
    }

    /**
     * Convenience method to return a boolean value for a given key
     *
     * @param key
     * @return true or false depending what was set. default is false
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * Convenience method to return a boolean value for a given key
     *
     * @param key
     * @param defaultResponse
     * @return true or false depending what was set. default defaultResponse
     */
    public boolean getBoolean(String key, boolean defaultResponse) {
        Boolean b = (Boolean) get(key);
        if (b != null) {
            return b.booleanValue();
        }
        return defaultResponse;
    }

    /**
     * sets a key value to Boolen.TRUE
     *
     * @param key
     */
    public void set(String key) {
        put(key, Boolean.TRUE);
    }

    /**
     * puts the key, value into the map. a null value will remove the key from the map
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        if (value != null) put(key, value);
        else remove(key);
    }

    /**
     * @return the HttpServletRequest from the "request" key
     */
    public HttpServletRequest getRequest() {
        return (HttpServletRequest) get(REQUEST_KEY);
    }

    /**
     * sets the HttpServletRequest into the "request" key
     *
     * @param request
     */
    public void setRequest(HttpServletRequest request) {
        put(REQUEST_KEY, request);
    }

    /**
     * @return the HttpServletResponse from the "response" key
     */
    public HttpServletResponse getResponse() {
        return (HttpServletResponse) get(RESPONSE_KEY);
    }

    /**
     * sets the "response" key to the HttpServletResponse passed in
     *
     * @param response
     */
    public void setResponse(HttpServletResponse response) {
        set(RESPONSE_KEY, response);
    }


    /**
     * unsets the threadLocal context. Done at the end of the request.
     */
    public void unset() {
        threadLocal.remove();
    }


}