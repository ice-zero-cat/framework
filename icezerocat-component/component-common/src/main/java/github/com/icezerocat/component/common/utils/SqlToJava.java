package github.com.icezerocat.component.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author 0.0.0
 * ProjectName: [easyexcel]
 * Package: [com.excel.easyexcel.utils.SqlToJava]
 * Description mysql数据类型转化JAVA
 * Date 2020/3/13 15:51
 */
@Slf4j
@SuppressWarnings("unused")
public class SqlToJava {
    /**
     * 数据类型转化JAVA
     *
     * @param sqlType：类型名称
     * @return 类型
     */
    public static String toSqlToJava(String sqlType) {
        if (sqlType == null || sqlType.trim().length() == 0) {
            return sqlType;
        }
        sqlType = sqlType.toLowerCase();
        switch (sqlType) {
            case "nvarchar":
            case "char":
            case "varchar":
            case "varchar2":
            case "text":
            case "nchar":
            case "longtext":
                return "String";
            case "blob":
            case "image":
                return "byte[]";
            case "integer":
            case "id":
            case "number":
                return "Long";
            case "int":
            case "tinyint":
            case "smallint":
            case "mediumint":
                return "Integer";
            case "bit":
            case "boolean":
                return "Boolean";
            case "bigint":
                return "java.math.BigInteger";
            case "float":
                return "Fload";
            case "double":
            case "money":
            case "smallmoney":
                return "Double";
            case "decimal":
            case "numeric":
            case "real":
                return "java.math.BigDecimal";
            case "date":
            case "datetime":
            case "year":
                return "java.util.Date";
            case "time":
                return "java.sql.Time";
            case "timestamp":
                return "java.sql.Timestamp";
            default:
                log.warn("-----------------》toSqlToJava转化失败：未发现的类型" + sqlType);
                return "String";
        }
    }

    /**
     * 数据类型转化JAVA
     *
     * @param sqlType：类型名称
     * @return 类型
     */
    public static String toSqlToJavaObjStr(String sqlType) {
        if (sqlType == null || sqlType.trim().length() == 0) {
            return sqlType;
        }
        sqlType = sqlType.toLowerCase();
        switch (sqlType) {
            case "nvarchar":
            case "char":
            case "varchar":
            case "varchar2":
            case "text":
            case "nchar":
            case "longtext":
                return String.class.getTypeName();
            case "blob":
            case "image":
                return byte[].class.getTypeName();
            case "bigint":
            case "integer":
            case "id":
            case "number":
                return Long.class.getTypeName();
            case "int":
                return int.class.getTypeName();
            case "tinyint":
            case "smallint":
            case "mediumint":
                return Integer.class.getTypeName();
            case "bit":
            case "boolean":
                return Boolean.class.getTypeName();
            case "float":
                return Float.class.getTypeName();
            case "decimal":
            case "double":
            case "money":
            case "smallmoney":
                return double.class.getTypeName();
            case "numeric":
            case "real":
                return BigDecimal.class.getTypeName();
            case "date":
            case "datetime":
            case "year":
                return Date.class.getTypeName();
            case "time":
                return Time.class.getTypeName();
            case "timestamp":
                return Timestamp.class.getTypeName();
            default:
                log.warn("-----------------》toSqlToJavaObjStr转化失败：未发现的类型" + sqlType);
                return String.class.getTypeName();
        }
    }
}
