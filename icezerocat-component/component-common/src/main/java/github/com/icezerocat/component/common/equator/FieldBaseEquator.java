package github.com.icezerocat.component.common.equator;

import com.esotericsoftware.reflectasm.MethodAccess;
import github.com.icezerocat.component.common.equator.annotations.EquatorName;
import github.com.icezerocat.component.common.equator.annotations.ExcludeField;
import github.com.icezerocat.component.common.utils.ReflectAsmUtil;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 基于属性的比对器
 *
 * @author zero
 * date 2018/11/22
 */
public class FieldBaseEquator extends AbstractEquator {

    public FieldBaseEquator() {
    }

    /**
     * 指定包含或排除某些字段
     *
     * @param includeFields 包含字段，若为 null 或空集，则不指定
     * @param excludeFields 排除字段，若为 null 或空集，则不指定
     */
    public FieldBaseEquator(List<String> includeFields, List<String> excludeFields) {
        super(includeFields, excludeFields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FieldInfo> getDiffFields(Object first, Object second) {
        Object obj = first == null ? second : first;
        Class<?> clazz = obj.getClass();
        if (first == second) {
            return Collections.emptyList();
        }
        // 先尝试判断是否为简单数据类型
        if (isSimpleField(clazz)) {
            return compareSimpleField(first, second);
        }

        List<FieldInfo> diffField = new LinkedList<>();
        // 获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        // 遍历所有的字段
        for (Field field : fields) {
            String fieldName = field.getName();
            try {
                // 开启访问权限，否则获取私有字段会报错
                field.setAccessible(true);
                Object firstVal = first == null ? null : field.get(first);
                Object secondVal = second == null ? null : field.get(second);
                // 封装字段信息
                FieldInfo fieldInfo = new FieldInfo(fieldName, field.getType(), firstVal, secondVal);
                //判断字段是否需要比较（ExcludeField排除比较），再比较字段值是否相等
                boolean eq = field.getDeclaredAnnotation(ExcludeField.class) != null || isFieldEquals(fieldInfo);
                if (!eq) {
                    // 记录不相等的字段
                    diffField.add(fieldInfo);
                }
            } catch (IllegalAccessException e) {
                // 只要调用了 field.setAccessible(true) 就不会报这个异常
                throw new IllegalStateException("获取属性进行比对发生异常: " + fieldName, e);
            }
        }
        return diffField;
    }

    /**
     * 获取不同的字段，以第一个对象注解为准
     *
     * @param source 原对象
     * @param target 目标对象
     * @return 不同字段
     */
    public List<FieldInfo> getDiffFieldsAndName(Object source, Object target) {

        if (source == null && target != null) {
            return this.getAllField(target, true);
        }

        if (target == null && source != null) {
            return this.getAllField(source, false);
        }

        assert source != null;
        Class<?> clazz = source.getClass();
        if (source == target) {
            return Collections.emptyList();
        }
        // 先尝试判断是否为简单数据类型
        if (isSimpleField(clazz)) {
            return compareSimpleField(source, target);
        }

        MethodAccess firstMethodAccess = ReflectAsmUtil.get(source.getClass());
        MethodAccess secondMethodAccess = ReflectAsmUtil.get(target.getClass());

        // 遍历所有的字段
        List<FieldInfo> diffField = new LinkedList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //此注解，忽略字段比较
            if (field.getDeclaredAnnotation(ExcludeField.class) != null) {
                continue;
            }
            String fieldName = field.getName();
            String methodName = "get" + StringUtils.capitalize(fieldName);
            try {
                // 开启访问权限，否则获取私有字段会报错
                field.setAccessible(true);
                Object firstVal = firstMethodAccess.invoke(source, methodName);
                Object secondVal = secondMethodAccess.invoke(target, methodName);
                EquatorName equatorName = field.getDeclaredAnnotation(EquatorName.class);
                //fieldName 值为空
                String equatorNameStr = equatorName != null ? equatorName.name() : fieldName;
                FieldInfo fieldInfo = new FieldInfo(equatorNameStr, fieldName, field.getType(), firstVal, secondVal);
                //判断字段是否需要比较（ExcludeField排除比较），再比较字段值是否相等
                boolean eq = isFieldEqualsSelective(fieldInfo);
                if (!eq) {
                    // 记录不相等的字段
                    diffField.add(fieldInfo);
                }
            } catch (Exception ignored) {
            }
        }
        return diffField;
    }

    /**
     * 获取对象全部字段
     *
     * @param o           对象
     * @param firstIsNull 第一个对象是否为空
     * @return 不同的字段列表
     */
    private List<FieldInfo> getAllField(Object o, boolean firstIsNull) {
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        Field[] declaredFields = o.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            String name = field.getName();
            try {
                Object value = field.get(o);
                EquatorName equatorName = field.getDeclaredAnnotation(EquatorName.class);
                String equatorNameStr = equatorName != null ? equatorName.name() : name;
                if (firstIsNull) {
                    fieldInfoList.add(new FieldInfo(equatorNameStr, name, field.getType(), null, value));
                } else {
                    fieldInfoList.add(new FieldInfo(equatorNameStr, name, field.getType(), value, null));
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return fieldInfoList;
    }
}
