package github.com.icezerocat.component.common.easyexcel.object;

import github.com.icezerocat.component.common.easyexcel.object.builder.AnnotationBuildType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 字段注解
 * CreateDate:  2020/7/20 19:37
 *
 * @author zero
 * @version 1.0
 */
@Data
public class FieldAnnotation implements Serializable {
    /**
     * 注解类名
     */
    private String className;
    /**
     * 注解成员
     */
    private List<AnnotationMember> annotationMemberList = new ArrayList<>();

    public FieldAnnotation() {
    }

    public FieldAnnotation(Build build) {
        this.className = build.className;
        this.annotationMemberList = build.annotationMemberList;
    }

    @Data
    public static class Build {
        /**
         * 注解类名
         */
        private String className;
        /**
         * 注解成员
         */
        private List<AnnotationMember> annotationMemberList = new ArrayList<>();

        public static Build getInstance(String annotationClassName) {
            Build build = new Build();
            build.setClassName(annotationClassName);
            return build;
        }

        /**
         * 添加注解
         *
         * @param value 值
         * @return build
         */
        public Build addAnnotationMember(Object value) {
            AnnotationMember annotationMember = new AnnotationMember();
            annotationMember.setValue(value);
            this.annotationMemberList.add(annotationMember);
            return this;
        }

        /**
         * 添加注解
         *
         * @param member 成员
         * @param value  值
         * @return build
         */
        public Build addAnnotationMember(String member, Object value) {
            AnnotationMember annotationMember = new AnnotationMember();
            annotationMember.setMember(member);
            annotationMember.setValue(value);
            this.annotationMemberList.add(annotationMember);
            return this;
        }

        /**
         * 添加注解
         *
         * @param member 成员
         * @param type   类型 {@link AnnotationBuildType}
         * @param value  值
         * @return build
         */
        public Build addAnnotationMember(String member, String type, Object value) {
            AnnotationMember annotationMember = new AnnotationMember();
            annotationMember.setMember(member);
            annotationMember.setType(type);
            annotationMember.setValue(value);
            this.annotationMemberList.add(annotationMember);
            return this;
        }

        /**
         * 添加注解
         *
         * @param member    成员
         * @param valueList 数组值
         * @return build
         */
        public Build addAnnotationMember(String member, List<Object> valueList) {
            AnnotationMember annotationMember = new AnnotationMember();
            annotationMember.setMember(member);
            annotationMember.setType(AnnotationBuildType.ARRAY.getValue().concat("_").concat(member));
            annotationMember.setValueList(valueList);
            this.annotationMemberList.add(annotationMember);
            return this;
        }

        /**
         * 完成构建
         *
         * @return httpResult
         */
        public FieldAnnotation complete() {
            return new FieldAnnotation(this);
        }
    }
}
