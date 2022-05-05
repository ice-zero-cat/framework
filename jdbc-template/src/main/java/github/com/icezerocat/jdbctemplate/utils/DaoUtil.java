package github.com.icezerocat.jdbctemplate.utils;

import github.com.icezerocat.jdbctemplate.annotations.TableName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by zmj
 * On 2019/8/20.
 *
 * @author 0.0.0
 */
@Slf4j
public class DaoUtil {

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
     * 获取表单名
     *
     * @param entityClass 对象类
     * @return 表单名
     */
    public static String getTableName(Class<?> entityClass) {
        String tableName;
        TableName annotation = entityClass.getAnnotation(TableName.class);
        if (annotation != null && StringUtils.isNotBlank(annotation.value())) {
            tableName = annotation.value();
        } else {
            tableName = StringUtil.camel2Underline(entityClass.getSimpleName());
        }
        return tableName;
    }
}
