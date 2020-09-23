package github.com.icezerocat.core.common.equator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ProjectName: [factory-boot-master]
 * Package:     [cn.oz.factory.factorycommon.equator.aop.ExcludeField]
 * Description: 排除字段比较注解
 * CreateDate:  2020/4/21 14:18
 *
 * @author 0.0.0
 * @version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcludeField {
}
