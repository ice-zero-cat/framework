package com.github.icezerocat.component.common.equator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: 比较字段注释名字
 * CreateDate:  2020/8/12 15:53
 *
 * @author zero
 * @version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EquatorName {

    /**
     * 注释名字
     *
     * @return 注释
     */
    String name() default "";
}
