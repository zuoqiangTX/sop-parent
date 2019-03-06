package com.gitee.sop.servercommon.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设置接口能力。
 * 如果想把已经存在的接口开放出去，可用此注解。<br>
 * 作用于Controller类上或方法上。如果作用在类上，则类中的所有方法将具备开放平台接口提供能力。
 * @author tanghc
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiAbility {
    /**
     * 版本号，如：1.0
     */
    String version() default "";
}
