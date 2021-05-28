package github.com.icezerocat.component.common.easyexcel.object;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 0.0.0
 * ProjectName: [easyexcel]
 * Package: [com.excel.easyexcel.object.EasyExcelWriterObject]
 * Description 导出对象
 * Date 2020/3/13 16:39
 */
@Data
public class ExcelWriter implements Serializable {
    /**
     * 字段名
     */
    private String field;
    /**
     * 数据类型
     */
    private String type;
    /**
     * 字段注解
     */
    private List<FieldAnnotation> fieldAnnotationList = new ArrayList<>();

    public ExcelWriter() {
    }

    /**
     * 构造函数
     *
     * @param build build对象
     */
    private ExcelWriter(Build build) {
        this.field = build.field;
        this.type = build.type;
        this.fieldAnnotationList = build.fieldAnnotationList;
    }

    @Data
    public static class Build {
        /**
         * 字段名
         */
        private String field;
        /**
         * 数据类型
         */
        private String type;
        /**
         * 字段注解
         */
        private List<FieldAnnotation> fieldAnnotationList = new ArrayList<>();

        /**
         * 构建Build
         *
         * @param field 字段
         */
        Build(String field) {
            this.field = field;
        }

        /**
         * 获取Build实例
         *
         * @param field 字段
         * @return Build
         */
        public static Build getInstance(String field) {
            return new Build(field);
        }

        /**
         * 设置字段类型
         *
         * @param type 字段类型
         * @return build
         */
        public Build setType(String type) {
            this.type = type;
            return this;
        }

        /**
         * 添加字段注解
         *
         * @param annotationClassName 字段注解全类名
         * @return build
         */
        public Build addFieldAnnotation(String annotationClassName) {
            FieldAnnotation fieldAnnotation = FieldAnnotation.Build.getInstance(annotationClassName).complete();
            this.fieldAnnotationList.add(fieldAnnotation);
            return this;
        }

        /**
         * 完成构建
         *
         * @return ExcelWriter 字段注解构建
         */
        public ExcelWriter complete() {
            return new ExcelWriter(this);
        }
    }
}
