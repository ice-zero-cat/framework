package github.com.icezerocat.core.service.impl;


import github.com.icezerocat.core.common.easyexcel.object.AnnotationMember;
import github.com.icezerocat.core.common.easyexcel.object.ExcelWriter;
import github.com.icezerocat.core.common.easyexcel.object.FieldAnnotation;
import github.com.icezerocat.core.common.easyexcel.object.Table;
import github.com.icezerocat.core.common.easyexcel.object.builder.FactoryAnnotationBuild;
import github.com.icezerocat.core.common.easyexcel.object.builder.JavassistBuilder;
import github.com.icezerocat.core.service.ClassService;
import github.com.icezerocat.core.service.DbService;
import github.com.icezerocat.core.utils.SqlToJava;
import github.com.icezerocat.core.utils.StringUtil;
import javassist.CtClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description: 类服务实现类
 * CreateDate:  2020/7/16 20:09
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@Service
public class ClassServiceImpl implements ClassService {

    private final DbService dbService;

    public ClassServiceImpl(DbService dbService) {
        this.dbService = dbService;
    }

    @Override
    public Class generateClass(String tableName, Class saveTargetClass) {
        String className = StringUtils.capitalize(StringUtil.underlineToCamelCase(tableName));
        return this.generateClass(tableName, className, null, null, saveTargetClass);
    }

    @Override
    public Class generateClass(String tableName, String className, Class saveTargetClass) {
        return this.generateClass(tableName, className, null, null, saveTargetClass);
    }

    @Override
    public Class generateClass(String tableName, String className, Map<String, String> fieldMapping, Class saveTargetClass) {
        return this.generateClass(tableName, className, fieldMapping, null, saveTargetClass);
    }

    @Override
    public Class generateClass(String tableName, String className, Map<String, String> fieldMapping, Map<String, String> typeMapping, Class saveTargetClass) {
        List<Map<String, String>> mapList = this.dbService.getTableField(tableName);
        JavassistBuilder javassistBuilder = new JavassistBuilder();
        //构建类
        JavassistBuilder.BuildClass buildClass = javassistBuilder.newBuildClass(className).setInterfaces(Serializable.class);

        //构建字段
        JavassistBuilder.BuildField buildField = javassistBuilder.newBuildField();
        mapList.forEach(m -> {
            String fieldType = m.get(Table.FIELDTYPE);
            String field = m.get(Table.FIELD);
            fieldType = SqlToJava.toSqlToJavaObjStr(fieldType);
            //字段映射：dbField —— entityField
            if (fieldMapping != null && fieldMapping.containsKey(field)) {
                field = fieldMapping.get(field);
            }
            //数据类型映射
            if (typeMapping != null && typeMapping.containsKey(field)) {
                fieldType = typeMapping.get(field);
            }
            buildField.addField(fieldType, field);
        });

        return this.writeField(buildClass, saveTargetClass);
    }

    @Override
    public Class generateClassByAnnotation(String tableName, String className, Map<String, ExcelWriter> excelWriterMap, Class saveTargetClass) {
        return this.generateClassByAnnotation(tableName, className, excelWriterMap, null, saveTargetClass);
    }

    @Override
    public Class generateClassByAnnotation(String tableName, String className, Map<String, ExcelWriter> excelWriterMap, Map<String, String> fieldMapping, Class saveTargetClass) {
        List<Map<String, String>> mapList = this.dbService.getTableField(tableName);
        JavassistBuilder javassistBuilder = new JavassistBuilder();
        //构建类
        JavassistBuilder.BuildClass buildClass = javassistBuilder.newBuildClass(className).setInterfaces(Serializable.class);

        //构建字段
        JavassistBuilder.BuildField buildField = javassistBuilder.newBuildField();
        for (Map<String, String> fieldData : mapList) {
            String field = fieldData.get(Table.FIELD);
            String fieldType = fieldData.get(Table.FIELDTYPE);
            String javaFieldType = SqlToJava.toSqlToJavaObjStr(fieldType);
            //数据值mapping
            if (fieldMapping != null && fieldMapping.containsKey(field)) {
                field = fieldMapping.get(field);
            }
            //判断字段是否有注解等属性
            if (excelWriterMap.containsKey(field)) {
                ExcelWriter excelWriter = excelWriterMap.get(field);
                String type = excelWriter.getType();
                //数据类型mapping
                if (type != null && !"".equals(type)) {
                    javaFieldType = type;
                }
                //添加字段和注解
                buildField.addField(javaFieldType, field);
                for (FieldAnnotation fieldAnnotation : excelWriter.getFieldAnnotationList()) {
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
            } else {
                //仅添加字段
                buildField.addField(javaFieldType, field);
            }
        }

        return writeField(buildClass, saveTargetClass);
    }


    @Override
    public Class writeField(JavassistBuilder.BuildClass buildClass, Class saveTargetClass) {
        if (saveTargetClass != null) {
            buildClass.writeFileByClass(saveTargetClass);
        } else {
            buildClass.writeFile();
        }
        CtClass ctClass = buildClass.getCtClass();
        Class easyExcelWriterObjectClass = null;
        try {
            easyExcelWriterObjectClass = Class.forName(ctClass.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return easyExcelWriterObjectClass;
    }

}
