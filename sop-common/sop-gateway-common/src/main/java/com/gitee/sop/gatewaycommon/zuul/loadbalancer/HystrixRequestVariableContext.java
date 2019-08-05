package com.gitee.sop.gatewaycommon.zuul.loadbalancer;

/**
 * @author tanghc
 */
public class HystrixRequestVariableContext {
//    private static final HystrixRequestVariableDefault<String> VERSION_HOLDER = new HystrixRequestVariableDefault<>();
    private static final ThreadLocal<String> VERSION_HOLDER = new ThreadLocal<>();

    public static void setVersion(String version) {
        VERSION_HOLDER.set(version);
    }

    public static String getVersion() {
        return VERSION_HOLDER.get();
    }

}
