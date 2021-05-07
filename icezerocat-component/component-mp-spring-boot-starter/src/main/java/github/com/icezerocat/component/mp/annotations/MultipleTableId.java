package github.com.icezerocat.component.mp.annotations;

import com.baomidou.mybatisplus.annotation.IdType;

import java.lang.annotation.*;

/**
 * Description: 多个主键
 * CreateDate:  2021/3/3 16:22
 *
 * @author zero
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MultipleTableId {
    /**
     * 字段值（驼峰命名方式，该值可无）
     *
     * @return id名
     */
    String value() default "";

    /**
     * 主键ID
     * {@link IdType}
     *
     * @return 主键生成方式
     */
    IdType type() default IdType.NONE;
}
