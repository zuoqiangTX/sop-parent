package com.gitee.sop.gatewaycommon.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author tanghc
 */
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return ctx.getBean(clazz);
    }

    public static Object getBean(String beanName) {
        return ctx.getBean(beanName);
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }
}
