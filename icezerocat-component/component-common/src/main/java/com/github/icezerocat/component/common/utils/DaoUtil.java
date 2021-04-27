package com.github.icezerocat.component.common.utils;

import com.github.icezerocat.component.common.builder.SearchBuild;
import com.github.icezerocat.component.common.equator.FieldInfo;
import com.github.icezerocat.component.common.model.Param;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

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
