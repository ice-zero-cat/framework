package github.com.icezerocat.mybatismp.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import github.com.icezerocat.core.common.easyexcel.object.Table;
import github.com.icezerocat.core.service.DbService;
import github.com.icezerocat.core.utils.SqlToJava;
import github.com.icezerocat.core.utils.StringUtil;
import github.com.icezerocat.core.utils.UploadUtil;
import github.com.icezerocat.mybatismp.model.javassist.build.JavassistBuilder;
import github.com.icezerocat.mybatismp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.mybatismp.config.ApplicationContextHelper;
import github.com.icezerocat.mybatismp.service.BaseMpBuildService;
import github.com.icezerocat.mybatismp.service.ProxyMpService;
import javassist.CtClass;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 基础mp构建服务
 * CreateDate:  2020/8/26 14:25
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@Service("baseMpBuildService")
public class BaseMpBuildServiceImpl implements BaseMpBuildService {

    /**
     * 记录动态注入的mapper
     */
    private static Map<String, NoahServiceImpl<BaseMapper<Object>, Object>> tableNameBaseMapperMap = new HashMap<>();
    private static Map<Class<?>, Object> entityBaseMapperMap = new HashMap<>();

    private final ProxyMpService proxyMpService;
    private final DbService dbService;

    private String prefix = "Ap";
    private String mapperPackage = JavassistBuilder.PACKAGE_NAME.concat(".mapper.");
    private String mapperService = JavassistBuilder.PACKAGE_NAME.concat(".service.");
    private String mapperServiceImpl = JavassistBuilder.PACKAGE_NAME.concat(".service.impl.");

    public BaseMpBuildServiceImpl(ProxyMpService proxyMpService, DbService dbService) {
        this.proxyMpService = proxyMpService;
        this.dbService = dbService;
    }

    @Override
    public NoahServiceImpl<BaseMapper<Object>, Object> newInstance(String tableName) {
        if (!tableNameBaseMapperMap.containsKey(tableName)) {
            String classesPath = this.getClassesPath();
            Object o = this.generateEntity(tableName.toLowerCase(), classesPath);
            NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.newInstance(o, classesPath);
            if (baseMapperObjectNoahService != null) {
                tableNameBaseMapperMap.put(tableName, baseMapperObjectNoahService);
            }
        }
        return tableNameBaseMapperMap.get(tableName);
    }

    @Override
    public <T> NoahServiceImpl<BaseMapper<T>, T> newInstance(T t) {
        return this.newInstance(t, this.getClassesPath());
    }

    /**
     * 创建service实例
     *
     * @param t           实体类
     * @param <T>         泛型实体类
     * @param classesPath target目标路径
     * @return NoahServiceImpl（增强版ServiceImpl-支持批量保存）
     */
    private <T> NoahServiceImpl<BaseMapper<T>, T> newInstance(T t, String classesPath) {
        if (!entityBaseMapperMap.containsKey(t.getClass())) {
            Class<BaseMapper<T>> baseMapperClass = this.generateMapper(t, classesPath);
            try {
                BaseMapper<T> tBaseMapper = this.proxyMpService.proxy(baseMapperClass);
                if (tBaseMapper != null) {
                    entityBaseMapperMap.put(t.getClass(), this.generateService(t, baseMapperClass, classesPath));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (NoahServiceImpl<BaseMapper<T>, T>) entityBaseMapperMap.get(t.getClass());
    }

    /**
     * 生成对象
     *
     * @param tableName   表单名
     * @param classesPath target路径
     * @return 对象
     */
    private Object generateEntity(String tableName, String classesPath) {
        Class oClass;
        List<Map<String, String>> mapList = this.dbService.getTableField(tableName);
        github.com.icezerocat.core.common.easyexcel.object.builder.JavassistBuilder javassistBuilder = new github.com.icezerocat.core.common.easyexcel.object.builder.JavassistBuilder();
        //构建类
        github.com.icezerocat.core.common.easyexcel.object.builder.JavassistBuilder.BuildClass buildClass =
                javassistBuilder.newBuildClass(StringUtils.capitalize(StringUtil.underlineToCamelCase(tableName))).setInterfaces(Serializable.class);
        buildClass.buildAnnotations(TableName.class).addMemberValue("value", tableName.toLowerCase()).commitAnnotation();

        //构建字段
        github.com.icezerocat.core.common.easyexcel.object.builder.JavassistBuilder.BuildField buildField = javassistBuilder.newBuildField();
        for (Map<String, String> fieldData : mapList) {
            //字段下划线转驼峰法
            String sourceField = fieldData.get(Table.FIELD);
            String field = StringUtil.underlineToCamelCase(sourceField);
            String fieldType = fieldData.get(Table.FIELDTYPE);
            String javaFieldType = SqlToJava.toSqlToJavaObjStr(fieldType);

            //id小写
            if ("ID".equals(field.toUpperCase())) {
                field = "id";
            }

            //添加myBatis字段和注解
            buildField.addField(javaFieldType, field);
            buildField.addAnnotation(TableField.class).addMemberValue("value", sourceField).commitAnnotation();
        }
        oClass = writeField(buildClass, classesPath);
        Object o = null;
        try {
            o = oClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("获取对象失败：{}", e.getMessage());
            e.printStackTrace();
        }
        return o;
    }

    /**
     * 生成baseMapper接口实例
     *
     * @param t           对象
     * @param <M>         baseMapper
     * @param <T>         实体类
     * @param classesPath target路径
     * @return baseMapper接口类
     */
    private <M extends BaseMapper<T>, T> Class<M> generateMapper(T t, String classesPath) {
        Class entityClass = t.getClass();
        String name = entityClass.getSimpleName().concat("Mapper");
        String packageName = this.mapperPackage.concat(this.prefix).concat(name);
        //构建泛型类
        TypeDescription.Generic genericSuperClass =
                TypeDescription.Generic.Builder.parameterizedType(BaseMapper.class, entityClass).build();
        ByteBuddy byteBuddy = new ByteBuddy();
        DynamicType.Unloaded<?> make = byteBuddy.makeInterface(genericSuperClass).name(packageName)
                .make();
        @SuppressWarnings("unchecked")
        Class<M> mClass = (Class<M>) this.generateClass(make, packageName, classesPath);
        return mClass;
    }

    /**
     * 生成service
     *
     * @param t           实体类
     * @param <T>         泛型实体类
     * @param classesPath target路径
     */
    private <E extends BaseMapper<T>, T> NoahServiceImpl<E, T> generateService(T t, Class<E> eClass, String classesPath) {
        Class entityClass = t.getClass();
        String simpleName = entityClass.getSimpleName();

        //生成service接口
        String serviceName = simpleName.concat("Service");
        String servicePackageName = this.mapperService.concat(this.prefix).concat(serviceName);
        TypeDescription.Generic iServiceGeneric =
                TypeDescription.Generic.Builder.parameterizedType(IService.class, entityClass).build();
        ByteBuddy serviceByteBuddy = new ByteBuddy();
        DynamicType.Unloaded<?> serviceMake = serviceByteBuddy.makeInterface(iServiceGeneric).name(servicePackageName).make();
        Class<?> serviceClass = this.generateClass(serviceMake, servicePackageName, classesPath);

        //生成serviceImpl实现类
        String serviceImplName = simpleName.concat("ServiceImpl");
        String serviceImplPackageName = this.mapperServiceImpl.concat(this.prefix).concat(serviceImplName);
        TypeDescription.Generic genericSuperClass =
                TypeDescription.Generic.Builder.parameterizedType(NoahServiceImpl.class, eClass, entityClass).build();
        ByteBuddy serviceImplByteBuddy = new ByteBuddy();
        DynamicType.Unloaded<?> serviceImplMake = serviceImplByteBuddy.subclass(genericSuperClass)
                .implement(TypeDescription.Generic.Builder.rawType(serviceClass).build())
                .name(serviceImplPackageName).make();
        @SuppressWarnings("unchecked")
        Class<NoahServiceImpl<E, T>> serviceImplClass = (Class<NoahServiceImpl<E, T>>) this.generateClass(serviceImplMake, serviceImplPackageName, classesPath);

        //注入bean
        return ApplicationContextHelper.registerBeanDefinitionByClass(serviceImplClass);
    }

    /**
     * 生成class
     *
     * @param make        动态构建
     * @param packageName 包名
     * @param classesPath target路径
     * @return 生成类
     */
    private Class<?> generateClass(DynamicType.Unloaded<?> make, String packageName, String classesPath) {
        Class<?> tClass = null;
        try {
            make.saveIn(new File(classesPath));
            tClass = Class.forName(packageName);
        } catch (IOException | ClassNotFoundException e) {
            log.debug("生成baseMapper class文件失败：{}", e.getMessage());
            e.printStackTrace();
        }
        return tClass;
    }

    /**
     * IO 写文件
     *
     * @param buildClass      构建类
     * @param saveTargetClass 当前项目任意一个字节码class
     * @return 类
     */
    private Class writeField(github.com.icezerocat.core.common.easyexcel.object.builder.JavassistBuilder.BuildClass buildClass,
                             String saveTargetClass
    ) {
        if (saveTargetClass == null) {
            buildClass.writeFile();
        } else {
            buildClass.writeFileByClass(saveTargetClass);
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

    /**
     * 获取生成字节码路径
     *
     * @return target路径
     */
    private String getClassesPath() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        return UploadUtil.getClassesPath(stackTraceElements[3].getClass());
    }
}