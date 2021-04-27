package com.github.icezerocat.component.common.utils;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ProjectName: [wln]
 * Package:     [cn.oz.fom.wln.utils.ReflectAsmUtil]
 * Description: 高性能的属性操作、方法调用、构造方法调用
 * CreateDate:  2020/4/2 11:56
 *
 * @author 0.0.0
 * @version 1.0
 */
@Slf4j
@SuppressWarnings("unused")
public class ReflectAsmUtil {

    private static final ConcurrentMap<Class, MethodAccess> LOCAL_CACHE = Maps.newConcurrentMap();
    private static Pattern underlinePattern = Pattern.compile("([A-Za-z\\d]+)(_)?");

    /**
     * MethodAccess类加载
     *
     * @param clazz 需要处理类
     * @return 返回MethodAccess对象操作类
     */
    public static MethodAccess get(Class clazz) {
        if (LOCAL_CACHE.containsKey(clazz)) {
            return LOCAL_CACHE.get(clazz);
        }

        MethodAccess methodAccess = MethodAccess.get(clazz);
        LOCAL_CACHE.putIfAbsent(clazz, methodAccess);
        return methodAccess;
    }

    /**
     * 复制类
     *
     * @param source 原对象
     * @param target 目标对象
     * @param <S>    原对象泛型
     * @param <T>    目标对象泛型
     */
    public static <S, T> void copyProperties(S source, T target) {
        MethodAccess fromMethodAccess = get(source.getClass());
        MethodAccess toMethodAccess = get(target.getClass());
        Field[] fromDeclaredFields = source.getClass().getDeclaredFields();
        for (Field field : fromDeclaredFields) {
            field.setAccessible(true);
            String name = field.getName();
            try {
                Object value = fromMethodAccess.invoke(source, "get" + StringUtils.capitalize(name));
                toMethodAccess.invoke(target, "set" + StringUtils.capitalize(name), value);
            } catch (Exception e) {
                // 设置异常，可能会没有对应字段，忽略
            }
        }

    }

    /**
     * 复制类
     *
     * @param source 原对象-支持下划线法、驼峰法、混合类
     * @param target 目标对象-驼峰法
     * @param <S>    原对象泛型
     * @param <T>    目标对象泛型
     */
    public static <S, T> void copyPropertiesByCamel(S source, T target) {
        MethodAccess fromMethodAccess = get(source.getClass());
        MethodAccess toMethodAccess = get(target.getClass());
        Field[] fromDeclaredFields = source.getClass().getDeclaredFields();
        for (Field field : fromDeclaredFields) {
            field.setAccessible(true);
            String name = field.getName();
            try {
                Object value = fromMethodAccess.invoke(source, "get" + StringUtils.capitalize(name));
                toMethodAccess.invoke(target, "set" + (name.contains("_") ? underline2Camel(name) : StringUtils.capitalize(name)), value);
            } catch (Exception e) {
                // 设置异常，可能会没有对应字段，忽略
            }
        }

    }

    /**
     * 对象转map(包含父类)
     *
     * @param o 对象
     * @return map(包含父类)
     */
    public static Map<String, Object> objectSup2Map(Object o) {
        Map<String, Object> oMap = new HashMap<>(16);
        Class oClass = o.getClass();
        while (oClass != null) {
            addMapValue(oMap, o, oClass);
            oClass = oClass.getSuperclass();
        }
        return null;
    }

    /**
     * 对象转map
     *
     * @param o 对象
     * @return map
     */
    public static Map<String, Object> object2Map(Object o) {
        Map<String, Object> oMap = new HashMap<>(16);
        Class oClass = o.getClass();
        addMapValue(oMap, o, oClass);
        return oMap;
    }

    private static void addMapValue(Map<String, Object> oMap, Object o, Class oClass) {
        Field[] declaredFields = oClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                Object fieldValue = field.get(o);
                oMap.put(fieldName, fieldValue);
            } catch (IllegalAccessException ignored) {

            }
        }
    }

    /**
     * 下划线转驼峰法
     *
     * @param line 源字符串
     * @return 转换后的字符串
     */
    private static String underline2Camel(String line) {
        if (line == null || "".equals(line)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = underlinePattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }
}
