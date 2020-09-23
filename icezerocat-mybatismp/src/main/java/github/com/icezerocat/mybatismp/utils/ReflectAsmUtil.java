package github.com.icezerocat.mybatismp.utils;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Maps;
import github.com.icezerocat.core.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
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
     * 复制类
     *
     * @param source       原对象
     * @param target       目标对象
     * @param targetPrefix 添加目标前缀
     * @param <S>          原对象泛型
     * @param <T>          目标对象泛型
     */
    public static <S, T> void copyProperties(S source, T target, String targetPrefix) {
        MethodAccess fromMethodAccess = get(source.getClass());
        MethodAccess toMethodAccess = get(target.getClass());
        Field[] fromDeclaredFields = source.getClass().getDeclaredFields();
        for (Field field : fromDeclaredFields) {
            field.setAccessible(true);
            String name = field.getName();
            try {
                Object value = fromMethodAccess.invoke(source, "get" + StringUtils.capitalize(name));
                toMethodAccess.invoke(target, "set" + StringUtils.capitalize(targetPrefix) + StringUtils.capitalize(name), value);
            } catch (Exception e) {
                // 设置异常，可能会没有对应字段，忽略
            }
        }

    }

    /**
     * 复制类
     *
     * @param map    map数据
     * @param target 目标对象
     * @param <T>    目标对象泛型
     */
    public static <T> void mapToBean(Map<String, Object> map, T target) {
        MethodAccess toMethodAccess = get(target.getClass());
        Field[] fromDeclaredFields = target.getClass().getDeclaredFields();
        for (Field field : fromDeclaredFields) {
            field.setAccessible(true);
            String name = field.getName();
            try {
                Object value = map.get(name);
                //判断属性类型 进行转换,map中存放的是Object对象需要转换 实体类中有多少类型就加多少类型,实体类属性用包装类;
                if (field.getType().toString().contains("Long")) {
                    value = Long.valueOf(String.valueOf(value));
                }
                //处理LocalDateTime类型
                if (field.getType().toString().contains("Date")) {
                    value = DateUtil.parseDate(String.valueOf(value));
                }
                toMethodAccess.invoke(target, "set" + StringUtils.capitalize(name), value);
            } catch (Exception e) {
                // 设置异常，可能会没有对应字段，忽略
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
