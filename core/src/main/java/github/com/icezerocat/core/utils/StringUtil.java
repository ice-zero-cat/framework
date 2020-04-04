package github.com.icezerocat.core.utils;


import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ProjectName: [sonatype]
 * Package: [com.zero.sonatype.utils.StringUtil]
 * Description: 字符串工具类
 * CreateDate: 2020/3/29 11:11
 *
 * @author 0.0.0
 * @version 1.0
 */
@SuppressWarnings("unused")
public class StringUtil {

    private static Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
    private static Pattern underlinePattern = Pattern.compile("([A-Za-z\\d]+)(_)?");
    private static Pattern isIntegerPattern = Pattern.compile("^[-+]?[\\d]*$");

    /**
     * 方法二：推荐，速度最快
     * 判断是否为整数（正数、负数）
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isInteger(String str) {
        return isIntegerPattern.matcher(str).matches();
    }

    /**
     * 首字母大写
     *
     * @param str 原字符串
     * @return 转换结果
     */
    public static String upperCaseFirst(String str) {
        return StringUtils.capitalize(str);
    }

    /**
     * 首字母小写
     *
     * @param str 原字符串
     * @return 转换结果
     */
    public static String lowerCaseFirst(String str) {
        return StringUtils.uncapitalize(str);
    }

    /**
     * 下划线，转换为驼峰式
     *
     * @param underscoreName 下划线命名
     * @return 驼峰法命名
     */
    public static String underlineToCamelCase(String underscoreName) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.trim().length() > 0) {
            boolean flag = false;
            for (int i = 0; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ("_".charAt(0) == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }

    /**
     * 对象转字符串
     *
     * @param t   实体类
     * @param <T> 泛型
     * @return 字符串
     */
    public static <T> String modelToString(T t) {
        StringBuilder result = new StringBuilder("[");

        for (Field declaredField : t.getClass().getDeclaredFields()) {
            try {
                result
                        .append(declaredField.getName())
                        .append("=")
                        .append(declaredField.get(t))
                        .append(",");
            } catch (IllegalAccessException e) {
                declaredField.setAccessible(true);
                try {
                    result
                            .append(declaredField.get(t))
                            .append(",");
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
                declaredField.setAccessible(false);
            }
        }

        return result.substring(0, result.length() - 1) + "]";
    }

    /**
     * 下划线转驼峰法
     *
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line) {
        if (line == null || "".equals(line)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = underlinePattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(matcher.start() == 0 ? Character.toLowerCase(word.charAt(0)) : Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰法转下划线
     *
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line) {
        if (line == null || "".equals(line)) {
            return "";
        }
        line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end() == line.length() ? "" : "_");
        }
        return sb.toString();
    }
}
