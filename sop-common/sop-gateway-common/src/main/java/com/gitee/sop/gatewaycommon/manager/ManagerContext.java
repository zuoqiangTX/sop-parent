package com.gitee.sop.gatewaycommon.manager;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * @author tanghc
 */
public class ManagerContext {

    private static ApplicationContext ctx;

    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static <T> T getManager(Class<T> clazz) {
        return ctx.getBean(clazz);
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }
}
