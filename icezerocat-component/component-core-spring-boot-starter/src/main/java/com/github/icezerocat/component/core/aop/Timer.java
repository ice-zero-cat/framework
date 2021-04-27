package com.github.icezerocat.component.core.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 0.0.0
 * ProjectName: [easyexcel]
 * Package: [com.excel.easyexcel.config.annotations.Timer]
 * Description 时间记录annotation
 * Date 2020/3/13 9:48
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Timer {
    String description() default "";
}
