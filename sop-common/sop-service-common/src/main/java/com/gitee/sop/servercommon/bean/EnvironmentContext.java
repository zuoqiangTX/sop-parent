package com.gitee.sop.servercommon.bean;

import org.springframework.core.env.Environment;

/**
 * @author thc
 */
public class EnvironmentContext {

    private static Environment environment;

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        EnvironmentContext.environment = environment;
    }

    public static String getProfile(Environment env) {
        return env.getProperty("spring.profiles.active", "default");
    }

    public static String getProfile() {
        return getProfile(environment);
    }
}