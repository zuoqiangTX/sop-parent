package com.gitee.sop.gatewaycommon.secret;

import com.gitee.sop.gatewaycommon.bean.IsvDefinition;

import java.util.function.Function;

/**
 * @author tanghc
 */
public class SecretContext {
    private static volatile Function<IsvDefinition, String> secretGetter = (isv) -> isv.getPubKey();

    public static Function<IsvDefinition, String> getSecretGetter() {
        return secretGetter;
    }

    public static void setSecretGetter(Function<IsvDefinition, String> secretGetter) {
        SecretContext.secretGetter = secretGetter;
    }
}
