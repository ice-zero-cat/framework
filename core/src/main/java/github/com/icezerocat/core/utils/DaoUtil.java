package github.com.icezerocat.core.utils;

import github.com.icezerocat.core.model.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zmj
 * On 2019/8/20.
 *
 * @author 0.0.0
 */
@Slf4j
public class DaoUtil {

    /**
     * 添加搜索
     *
     * @param list      list数组
     * @param hql       hql语句
     * @param searchMap 搜索条件
     */
    public static void addSearch(List<Object> list, StringBuilder hql, Map<String, Object> searchMap) {
        //筛选条件
        for (String key : searchMap.keySet()) {
            //判断对象是否是日期类型（只适应于day，不用于月）
            if (searchMap.get(key) instanceof Date) {
                Date leftDate = (Date) searchMap.get(key);
                Calendar rightDate = Calendar.getInstance();
                rightDate.setTime(leftDate);
                //天数自增
                rightDate.add(Calendar.DAY_OF_MONTH, 1);

                hql.append(" and ").append(key).append(" >=").append(" ?").append(" and ").append(key).append(" <").append(" ?");
                list.add(leftDate);
                list.add(rightDate.getTime());
            } else {
                Object object = searchMap.get(key);
                if (!StringUtils.isEmpty(StringUtils.trimWhitespace(String.valueOf(object)))) {
                    hql.append(" and ").append(key).append(" like").append(" '%").append(object).append("%'");
                }
            }
        }
    }

    public static double formatDouble(double d) {
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.DOWN);
        return bg.doubleValue();
    }

    /**
     * 反射调用动态方法
     *
     * @param object     类
     * @param methodName 方法名
     * @param params     方法传入参数的类型 可指定一个，也可以指定多个，但一定要一一类型对应
     * @return 方法返回值
     */
    public static Object invoke(Object object, String methodName, Object... params) {
        try {
            //获取类中的所有方法
            Method[] methods = object.getClass().getDeclaredMethods();
            //需要调用的方法
            Method callMethod = null;
            for (Method method : methods) {
                //类中是否有方法名为[methodName]的方法
                if (method.getName().equals(methodName)) {
                    callMethod = method;
                    break;
                }
            }
            if (callMethod != null) {
                //设置可访问
                callMethod.setAccessible(true);
                return callMethod.invoke(object, params);
            }
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                cause.printStackTrace();
            }
            if (e instanceof InvocationTargetException) {
                Throwable t = ((InvocationTargetException) e).getTargetException();
                if (t != null) {
                    t.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射调用动态方法
     *
     * @param object     类
     * @param methodName 方法名
     * @return 方法返回值
     */
    public static Object invokeMethod(Object object, String methodName, List<Search> searchList, Pageable pageable) {
        try {
            //获取方法包括私有、父类
            Method method = object.getClass().getDeclaredMethod(methodName, List.class, Pageable.class);
            method.setAccessible(true);
            return method.invoke(object, searchList, pageable);
        } catch (NoSuchMethodException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                cause.printStackTrace();
            }
            log.error(object.getClass().getName() + " 找不到 " + methodName + " 方法! " + "\t" + e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                cause.printStackTrace();
            }
            if (e instanceof InvocationTargetException) {
                Throwable t = ((InvocationTargetException) e).getTargetException();
                if (t != null) {
                    t.printStackTrace();
                }
            }
            log.error(object.getClass().getName() + " 调用 " + methodName + " 方法失败！ " + "\t" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取表单名
     *
     * @param entityClass 对象类
     * @return 表单名
     */
    public static String getTableName(Class<?> entityClass) {
        String tableName;
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null) {
            tableName = table.name();
        } else {
            tableName = StringUtil.camel2Underline(entityClass.getSimpleName());
        }
        return tableName;
    }

    /**
     * 字符串获取class
     *
     * @param packageName 包名
     * @param className   类名
     * @return 类对象
     */
    public static Object getClass(String packageName, String className) {

        //获取实现类
        String classFullName = packageName + toFirstUpperCase(className);
        Object object = new Object();
        try {
            object = Class.forName(classFullName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error("JdbcReportServiceImpl中找不到类：{}", classFullName);
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 左连接语句
     *
     * @param sqlBuilder sql
     * @param onString   关联条件
     */
    public static void leftJoin(StringBuilder sqlBuilder, String tableName, String aliasName, String onString) {
        sqlBuilder.append(" LEFT JOIN ").append(tableName).append(" ").append(aliasName).append(" ").append(" ON ").append(onString).append(" ");
    }

    /**
     * 首字母大写
     *
     * @param str 字符串
     * @return 首字母大写字符串
     */
    public static String toFirstUpperCase(String str) {
        //首字母大写
        char[] chars = str.toCharArray();
        if (chars[0] >= 'a' && chars[0] <= 'z') {
            chars[0] = (char) (chars[0] - 32);
        }
        return new String(chars);
    }

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

}
