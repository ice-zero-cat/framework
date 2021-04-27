package com.github.icezerocat.component.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Description: 转换类型工具类
 * CreateDate:  2020/10/31 8:05
 *
 * @author zero
 * @version 1.0
 */
@SuppressWarnings("unused")
public class ConverterUtil {

    /**
     * map转对象
     *
     * @param map   map
     * @param clazz 类
     * @return 对象
     */
    public static Object map2Object(Map<String, Object> map, Class<?> clazz) {
        if (map == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = clazz.newInstance();

            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

}
