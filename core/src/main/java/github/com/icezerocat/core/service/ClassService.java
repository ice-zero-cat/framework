package github.com.icezerocat.core.service;


import github.com.icezerocat.core.common.easyexcel.object.ExcelWriter;

import java.util.Map;

/**
 * Description: 类服务
 * CreateDate:  2020/7/16 20:08
 *
 * @author zero
 * @version 1.0
 */
public interface ClassService {
    /**
     * 生成类
     *
     * @param tableName       表名
     * @param saveTargetClass 保存类字节码文件
     * @return 返回生成类
     */
    Class generateClass(String tableName, Class saveTargetClass);

    /**
     * 生成类
     *
     * @param tableName       表名
     * @param className       类名
     * @param saveTargetClass 保存类字节码文件
     * @return 返回生成类
     */
    Class generateClass(String tableName, String className, Class saveTargetClass);

    /**
     * 生成类
     *
     * @param tableName       表名
     * @param className       类名
     * @param fieldMapping    导出字段属性（字段类型，注解）
     * @param saveTargetClass 保存类字节码文件
     * @return 返回生成类
     */
    Class generateClass(String tableName, String className, Map<String, String> fieldMapping, Class saveTargetClass);

    /**
     * 生成类
     *
     * @param tableName       表名
     * @param className       类名
     * @param fieldMapping    导出字段属性（字段类型，注解）
     * @param typeMapping     数据类型转换
     * @param saveTargetClass 保存类字节码文件
     * @return 返回生成类
     */
    Class generateClass(String tableName, String className, Map<String, String> fieldMapping, Map<String, String> typeMapping, Class saveTargetClass);

    /**
     * 生成类
     *
     * @param tableName       表名
     * @param className       类名
     * @param excelWriterMap  导出字段属性（字段类型，注解）
     * @param saveTargetClass 保存类字节码文件
     * @return 生成类
     */
    Class generateClassByAnnotation(String tableName, String className, Map<String, ExcelWriter> excelWriterMap, Class saveTargetClass);

    /**
     * 生成类
     *
     * @param tableName       表名
     * @param className       类名
     * @param excelWriterMap  导出字段属性（字段类型，注解）
     * @param fieldMapping    字段映射类
     * @param saveTargetClass 保存类字节码文件
     * @return 生成类
     */
    Class generateClassByAnnotation(String tableName, String className, Map<String, ExcelWriter> excelWriterMap, Map<String, String> fieldMapping, Class saveTargetClass);
}
