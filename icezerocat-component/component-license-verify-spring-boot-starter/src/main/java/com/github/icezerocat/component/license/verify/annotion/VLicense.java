package com.github.icezerocat.component.license.verify.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: 认证注解
 * CreateDate:  2021/8/31 22:48
 *
 * @author zero
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VLicense {
    /**
     * 认证
     *
     * @return 认证数组
     */
    String[] verifies() default {};
}
