package github.com.icezerocat.core.utils;

import com.github.dadiyang.equator.FieldInfo;
import github.com.icezerocat.core.builder.SearchBuild;
import github.com.icezerocat.core.model.Param;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import javax.persistence.Table;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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
     * 反射调用动态方法
     *
     * @param object     类
     * @param methodName 方法名
     * @param searchList 搜索参数
     * @param pageable   分页对象
     * @return 方法返回值
     */
    public static Object invokeMethod(Object object, String methodName, List<Param> searchList, Pageable pageable) {
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
     * 构建更新sql（部分字段更新）
     * 注：目前仅对String操作，其他类型未测试
     *
     * @param tableName  表名
     * @param diffFields 不同的字段类。更新字段:fieldInfo.getFieldName(),值：fieldInfo.getSecondVal()
     * @param params     条件参数
     * @return update sql
     */
    public static String buildUpdateSql(String tableName, List<FieldInfo> diffFields, List<Param> params) {
        String returnSql;
        StringBuilder sql = new StringBuilder().append(" update ").append(tableName).append(" set ");
        for (FieldInfo fieldInfo : diffFields) {
            sql.append(StringUtil.camel2Underline(fieldInfo.getFieldName())).append(" = ")
                    .append(" \"").append(fieldInfo.getSecondVal()).append("\" ").append(" , ");
        }
        sql.delete(sql.length() - 2, sql.length());
        sql.append(" where 1 = 1 ");
        returnSql = sql.toString();
        if (!CollectionUtils.isEmpty(params)) {
            SearchBuild builder = new SearchBuild.Builder().operation(sql.toString()).searchList(params).start();
            returnSql = builder.getHql();
        }
        return returnSql;
    }
}
