package github.com.icezerocat.component.common.model;

import github.com.icezerocat.component.common.easyexcel.object.ExcelWriter;
import github.com.icezerocat.component.common.easyexcel.object.FieldAnnotation;
import github.com.icezerocat.component.common.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Description: AP类构建
 * CreateDate:  2021/5/7 15:59
 *
 * @author zero
 * @version 1.0
 */
@Data
@AllArgsConstructor
@SuppressWarnings("unused")
public class ApClassModel implements Serializable {
    /**
     * 表名
     */
    final private String tableName;
    /**
     * 类名
     */
    final private String className;
    /**
     * 导出字段属性（字段类型，注解）
     */
    final private Map<String, ExcelWriter> excelWriterMap;
    /**
     * 类注解列表
     */
    final private List<FieldAnnotation> classAnnotationList;
    /**
     * 字段默认注解
     */
    final private List<FieldAnnotation> fieldDefaultAnnotationList;
    /**
     * 排除默认注解字段
     */
    final private List<String> excludeDefaultAnnotationFieldList;

    private ApClassModel(Build build) {
        this.tableName = build.tableName;
        this.className = build.className;
        this.excelWriterMap = build.excelWriterMap;
        this.classAnnotationList = build.classAnnotationList;
        this.fieldDefaultAnnotationList = build.fieldDefaultAnnotationList;
        this.excludeDefaultAnnotationFieldList = build.excludeDefaultAnnotationFieldList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Build implements Serializable {
        /**
         * 表名
         */
        private String tableName;
        /**
         * 类名
         */
        private String className;
        /**
         * 类注解列表
         */
        private List<FieldAnnotation> classAnnotationList = new ArrayList<>();
        /**
         * 导出字段属性（字段类型，注解）
         */
        private Map<String, ExcelWriter> excelWriterMap = new HashMap<>();
        /**
         * 字段默认注解
         */
        private List<FieldAnnotation> fieldDefaultAnnotationList = new ArrayList<>();
        /**
         * 排除默认注解字段
         */
        private List<String> excludeDefaultAnnotationFieldList = new ArrayList<>();

        Build(String tableName) {
            this.tableName = tableName;
        }

        /**
         * 获取实例
         *
         * @param tableName 表名
         * @return 构建实例
         */
        public static Build getInstance(String tableName) {
            return new Build(tableName);
        }

        /**
         * 获取驼峰实例
         *
         * @param tableName 表名
         * @return 构建实例
         */
        public static Build getCamelInstance(String tableName) {
            tableName = StringUtils.capitalize(StringUtil.underline2Camel(tableName));
            return getInstance(tableName);
        }

        /**
         * 设置className
         *
         * @param className 类名
         * @return build
         */
        public Build setClassName(String className) {
            this.className = className;
            return this;
        }

        /**
         * 设置自定义映射字段
         *
         * @param excelWriterMap 映射对象
         * @return build
         */
        public Build setExcelWriterMap(Map<String, ExcelWriter> excelWriterMap) {
            if (Objects.nonNull(excelWriterMap)) {
                this.excelWriterMap = excelWriterMap;
            }
            return this;
        }

        /**
         * 设置类注解列表
         *
         * @param classAnnotationList 类注解列表
         * @return build
         */
        public Build setClassAnnotationList(List<FieldAnnotation> classAnnotationList) {
            this.classAnnotationList = classAnnotationList;
            return this;
        }

        /**
         * 设置字段默认注解
         *
         * @param fieldDefaultAnnotationList 字段默认注解列表
         * @return build
         */
        public Build setFieldDefaultAnnotationList(List<FieldAnnotation> fieldDefaultAnnotationList) {
            this.fieldDefaultAnnotationList = fieldDefaultAnnotationList;
            return this;
        }

        /**
         * 设置排除默认注解字段
         *
         * @param excludeDefaultAnnotationFieldList 排除默认注解字段列表
         * @return build
         */
        public Build setExcludeDefaultAnnotationFieldList(List<String> excludeDefaultAnnotationFieldList) {
            this.excludeDefaultAnnotationFieldList = excludeDefaultAnnotationFieldList;
            return this;
        }

        /**
         * 完成构建
         *
         * @return httpResult
         */
        public ApClassModel complete() {
            return new ApClassModel(this);
        }
    }
}
