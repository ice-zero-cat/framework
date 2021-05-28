package github.com.icezerocat.component.db.service.impl;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import github.com.icezerocat.component.common.easyexcel.object.AnnotationMember;
import github.com.icezerocat.component.common.easyexcel.object.ExcelWriter;
import github.com.icezerocat.component.common.easyexcel.object.FieldAnnotation;
import github.com.icezerocat.component.common.easyexcel.object.Table;
import github.com.icezerocat.component.common.easyexcel.object.builder.AnnotationBuildType;
import github.com.icezerocat.component.common.model.ApClassModel;
import github.com.icezerocat.component.common.utils.DateJacksonConverter;
import github.com.icezerocat.component.common.utils.SqlToJava;
import github.com.icezerocat.component.common.utils.StringUtil;
import github.com.icezerocat.component.db.builder.FactoryAnnotationBuild;
import github.com.icezerocat.component.db.builder.JavassistBuilder;
import github.com.icezerocat.component.db.service.ClassService;
import github.com.icezerocat.component.db.service.DbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Description: 类服务实现类
 * CreateDate:  2020/7/16 20:09
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
public class ClassServiceImpl implements ClassService {

    private final DbService dbService;

    public ClassServiceImpl(DbService dbService) {
        this.dbService = dbService;
    }

    @Override
    public Class generateClass(String tableName) {
        return this.generateClass(ApClassModel.Build.getInstance(tableName).complete());
    }

    @Override
    public Class generateClass(ApClassModel apClassModel) {
        String className = apClassModel.getClassName();
        String tableName = apClassModel.getTableName();
        List<String> excludeDefaultAnnotationFieldList = apClassModel.getExcludeDefaultAnnotationFieldList();
        List<FieldAnnotation> fieldDefaultAnnotationList = apClassModel.getFieldDefaultAnnotationList();
        Map<String, ExcelWriter> excelWriterMap = apClassModel.getExcelWriterMap();
        className = className == null ? StringUtils.capitalize(StringUtil.underline2Camel(tableName)) : className;
        excelWriterMap = excelWriterMap == null ? new HashMap<>(1) : excelWriterMap;

        List<Map<String, String>> mapList = this.dbService.getTableField(tableName);
        JavassistBuilder javassistBuilder = new JavassistBuilder();
        //构建类
        JavassistBuilder.BuildClass buildClass = this.buildClass(javassistBuilder, className, apClassModel.getClassAnnotationList());

        //构建字段
        JavassistBuilder.BuildField buildField = javassistBuilder.newBuildField();
        for (Map<String, String> fieldData : mapList) {
            //字段下划线转驼峰法
            String sourceField = fieldData.get(Table.FIELD);
            String field = StringUtil.underline2Camel(sourceField);
            String fieldType = fieldData.get(Table.FIELDTYPE);
            String javaFieldType = SqlToJava.toSqlToJavaObjStr(fieldType);

            //日期类型添加日期注解
            this.addDateAnnotation(field, javaFieldType, excelWriterMap);

            //添加默认注解
            if (!excludeDefaultAnnotationFieldList.contains(field) && CollectionUtils.isEmpty(fieldDefaultAnnotationList)) {
                this.addFieldAnnotation(buildField, fieldDefaultAnnotationList);
            }

            //判断字段
            if (excelWriterMap.containsKey(field)) {
                this.addSpecialField(field, javaFieldType, excelWriterMap, buildField);
            } else {
                //仅添加字段
                buildField.addField(javaFieldType, field);
            }
        }
        return buildClass.writeFile();
    }


    /**
     * 构建类
     *
     * @param javassistBuilder    javassist生成器
     * @param className           类名
     * @param classAnnotationList 类注解列表
     * @return 类
     */
    private JavassistBuilder.BuildClass buildClass(JavassistBuilder javassistBuilder, String className, List<FieldAnnotation> classAnnotationList) {
        JavassistBuilder.BuildClass buildClass = javassistBuilder.newBuildClass(className).setInterfaces(Serializable.class);
        classAnnotationList.forEach(fieldAnnotation -> {
            try {
                buildClass.addAnnotation(Class.forName(fieldAnnotation.getClassName()));
            } catch (ClassNotFoundException e) {
                log.error("generateClass注解类名错误： {}", fieldAnnotation.getClassName());
                e.printStackTrace();
            }
            for (AnnotationMember annotationMember : fieldAnnotation.getAnnotationMemberList()) {
                FactoryAnnotationBuild.addMemberValue(buildClass, annotationMember);
            }
            buildClass.commitAnnotation();
        });
        return buildClass;
    }

    /**
     * 添加日期注解
     *
     * @param field          字段名字
     * @param javaFieldType  字段类型
     * @param excelWriterMap 注解存储容器
     */
    private void addDateAnnotation(String field, String javaFieldType, Map<String, ExcelWriter> excelWriterMap) {
        if (Date.class.getTypeName().equalsIgnoreCase(javaFieldType)) {
            ExcelWriter excelWriter = new ExcelWriter();
            excelWriter.setField(field);
            List<FieldAnnotation> fieldAnnotationList = new ArrayList<>();
            //字段注解
            FieldAnnotation fieldAnnotation = new FieldAnnotation();
            fieldAnnotation.setClassName(JsonDeserialize.class.getName());
            //注解成员属性
            AnnotationMember annotationMember = new AnnotationMember();
            annotationMember.setMember("using");
            annotationMember.setType(AnnotationBuildType.CLASS.getValue());
            annotationMember.setValue(DateJacksonConverter.class);
            fieldAnnotation.setAnnotationMemberList(Collections.singletonList(annotationMember));
            fieldAnnotationList.add(fieldAnnotation);
            excelWriter.setFieldAnnotationList(fieldAnnotationList);
            //不存在则创建，存在则追加@JsonDeserialize注解
            if (!excelWriterMap.containsKey(field)) {
                excelWriterMap.put(field, excelWriter);
            } else {
                excelWriterMap.get(field).getFieldAnnotationList().add(fieldAnnotation);
            }
        }
    }

    /**
     * 特殊字段：自定义字段名、字段类型、注解
     *
     * @param field          字段
     * @param javaFieldType  字段类型
     * @param excelWriterMap 注解容器
     * @param buildField     构建字段类
     */
    private void addSpecialField(String field, String javaFieldType, Map<String, ExcelWriter> excelWriterMap, JavassistBuilder.BuildField buildField) {
        ExcelWriter excelWriter = excelWriterMap.get(field);
        String type = excelWriter.getType();
        String newField = excelWriter.getField();
        //数据类型mapping
        if (type != null && !"".equals(type)) {
            javaFieldType = type;
        }
        //自定义字段mapping
        if (org.apache.commons.lang3.StringUtils.isNotBlank(newField)) {
            field = newField;
        }
        //添加字段
        buildField.addField(javaFieldType, field);

        //添加字段的注解
        this.addFieldAnnotation(buildField, excelWriter.getFieldAnnotationList());

    }

    /**
     * 添加字段的注解
     *
     * @param buildField          字段构建对象
     * @param fieldAnnotationList 注解列表
     */
    private void addFieldAnnotation(JavassistBuilder.BuildField buildField, List<FieldAnnotation> fieldAnnotationList) {
        for (FieldAnnotation fieldAnnotation : fieldAnnotationList) {
            try {
                buildField.addAnnotation(Class.forName(fieldAnnotation.getClassName()));
            } catch (ClassNotFoundException e) {
                log.error("generateClass注解类名错误： {}", fieldAnnotation.getClassName());
                e.printStackTrace();
            }
            for (AnnotationMember annotationMember : fieldAnnotation.getAnnotationMemberList()) {
                FactoryAnnotationBuild.addMemberValue(buildField, annotationMember);
            }
            buildField.commitAnnotation();
        }
    }

}
