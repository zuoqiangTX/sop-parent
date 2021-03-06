package com.gitee.sop.gatewaycommon.bean;

import org.springframework.context.ApplicationContext;

/**
 * spring 上下文
 *
 * @author tanghc
 */
public class SpringContext {

    private static ApplicationContext ctx;

    public static <T> T getBean(Class<T> clazz) {
        return ctx.getBean(clazz);
    }

    public static Object getBean(String beanName) {
        return ctx.getBean(beanName);
    }

    public static void setApplicationContext(ApplicationContext ctx) {
        SpringContext.ctx = ctx;
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }
}
