package github.com.icezerocat.component.mp.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import github.com.icezerocat.component.common.easyexcel.object.Table;
import github.com.icezerocat.component.common.utils.ClassUtils;
import github.com.icezerocat.component.common.utils.SqlToJava;
import github.com.icezerocat.component.common.utils.StringUtil;
import github.com.icezerocat.component.core.config.ProjectPathConfig;
import github.com.icezerocat.component.db.builder.JavassistBuilder;
import github.com.icezerocat.component.db.service.DbService;
import github.com.icezerocat.component.mp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.component.mp.config.MpApplicationContextHelper;
import github.com.icezerocat.component.mp.service.BaseMpBuildService;
import github.com.icezerocat.component.mp.service.ProxyMpService;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.apache.ibatis.annotations.Mapper;
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
    private String mapperPackage = JavassistBuilder.PACKAGE_NAME.concat("mapper.");
    private String mapperService = JavassistBuilder.PACKAGE_NAME.concat("service.");
    private String mapperServiceImpl = JavassistBuilder.PACKAGE_NAME.concat("service.impl.");

    public BaseMpBuildServiceImpl(ProxyMpService proxyMpService, DbService dbService) {
        this.proxyMpService = proxyMpService;
        this.dbService = dbService;
    }

    @Override
    public NoahServiceImpl<BaseMapper<Object>, Object> newInstance(String tableName) {
        if (!tableNameBaseMapperMap.containsKey(tableName)) {
            Object o = this.generateEntity(tableName.toLowerCase());
            NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.newInstance(o);
            if (baseMapperObjectNoahService != null) {
                tableNameBaseMapperMap.put(tableName, baseMapperObjectNoahService);
            }
        }
        return tableNameBaseMapperMap.get(tableName);
    }

    /**
     * 创建service实例
     *
     * @param t   实体类
     * @param <T> 泛型实体类
     * @return NoahServiceImpl（增强版ServiceImpl-支持批量保存）
     */
    @Override
    public <T> NoahServiceImpl<BaseMapper<T>, T> newInstance(T t) {
        //jvm缓存是否存在
        if (!entityBaseMapperMap.containsKey(t.getClass())) {
            //判断class缓存是否已经初始化过
            String beanName = StringUtils.uncapitalize(this.getServiceName(t.getClass()));
            Object bean = MpApplicationContextHelper.getBean(beanName);
            if (bean != null) {
                entityBaseMapperMap.put(t.getClass(), bean);
            } else {
                Class<BaseMapper<T>> baseMapperClass = this.generateMapper(t);
                try {
                    BaseMapper<T> tBaseMapper = this.proxyMpService.proxy(baseMapperClass);
                    entityBaseMapperMap.put(t.getClass(), this.generateService(t, baseMapperClass, tBaseMapper));
                } catch (Exception e) {
                    log.error("proxy NoahServiceImpl failed : {}", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return (NoahServiceImpl<BaseMapper<T>, T>) entityBaseMapperMap.get(t.getClass());
    }

    /**
     * 生成对象
     *
     * @param tableName 表单名
     * @return 对象
     */
    private Object generateEntity(String tableName) {
        Class oClass;
        List<Map<String, String>> mapList = this.dbService.getTableField(tableName);
        JavassistBuilder javassistBuilder = new JavassistBuilder();
        //构建类
        JavassistBuilder.BuildClass buildClass =
                javassistBuilder.newBuildClass(StringUtils.capitalize(StringUtil.underlineToCamelCase(tableName))).setInterfaces(Serializable.class);
        buildClass.buildAnnotations(TableName.class).addMemberValue("value", tableName.toLowerCase()).commitAnnotation();

        //构建字段
        JavassistBuilder.BuildField buildField = javassistBuilder.newBuildField();
        for (Map<String, String> fieldData : mapList) {
            //字段下划线转驼峰法
            String sourceField = fieldData.get(Table.FIELD);
            String field = StringUtil.underline2Camel(sourceField);
            String fieldType = fieldData.get(Table.FIELDTYPE);
            String javaFieldType = SqlToJava.toSqlToJavaObjStr(fieldType);

            //id小写
            if ("ID".equals(field.toUpperCase())) {
                field = "id";
                buildField.addField(javaFieldType, field);
                buildField.addAnnotation(TableId.class).addMemberValue("value", sourceField).commitAnnotation();
            }else {
                //添加myBatis字段和注解
                buildField.addField(javaFieldType, field);
                buildField.addAnnotation(TableField.class).addMemberValue("value", sourceField).commitAnnotation();
            }
        }
        oClass = buildClass.writeFile();
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
     * @param t   对象
     * @param <M> baseMapper
     * @param <T> 实体类
     * @return baseMapper接口类
     */
    private <M extends BaseMapper<T>, T> Class<M> generateMapper(T t) {
        Class entityClass = t.getClass();
        String name = this.getMapperName(entityClass);
        String packageName = this.mapperPackage.concat(name);
        //构建泛型类
        TypeDescription.Generic genericSuperClass =
                TypeDescription.Generic.Builder.parameterizedType(BaseMapper.class, entityClass).build();
        ByteBuddy byteBuddy = new ByteBuddy();
        DynamicType.Unloaded<?> make = byteBuddy.makeInterface(genericSuperClass).name(packageName)
                .annotateType(AnnotationDescription.Builder.ofType(Mapper.class).build())
                .make();
        @SuppressWarnings("unchecked")
        Class<M> mClass = (Class<M>) this.generateClass(make, packageName);
        return mClass;
    }

    /**
     * 生成service
     *
     * @param t      实体类
     * @param <T>    泛型实体类
     * @param eClass mapper接口类
     * @param e      mapper实现类实例
     */
    private <E extends BaseMapper<T>, T> NoahServiceImpl<E, T> generateService(T t, Class<E> eClass, E e) {
        Class entityClass = t.getClass();
        //生成service接口
        String serviceName = this.getServiceName(entityClass);
        String servicePackageName = this.mapperService.concat(serviceName);
        TypeDescription.Generic iServiceGeneric =
                TypeDescription.Generic.Builder.parameterizedType(IService.class, entityClass).build();
        ByteBuddy serviceByteBuddy = new ByteBuddy();
        DynamicType.Unloaded<?> serviceMake = serviceByteBuddy.makeInterface(iServiceGeneric).name(servicePackageName).make();
        Class<?> serviceClass = this.generateClass(serviceMake, servicePackageName);

        //生成serviceImpl实现类
        String serviceImplName = this.getServiceImplName(entityClass);
        String serviceImplPackageName = this.mapperServiceImpl.concat(serviceImplName);
        TypeDescription.Generic genericSuperClass =
                TypeDescription.Generic.Builder.parameterizedType(NoahServiceImpl.class, eClass, entityClass).build();
        ByteBuddy serviceImplByteBuddy = new ByteBuddy();
        DynamicType.Unloaded<?> serviceImplMake = serviceImplByteBuddy.subclass(genericSuperClass)
                .implement(TypeDescription.Generic.Builder.rawType(serviceClass).build())
                .name(serviceImplPackageName)
                .annotateType(AnnotationDescription.Builder.ofType(Service.class).define("value", StringUtils.uncapitalize(serviceName)).build())
                .make();
        @SuppressWarnings("unchecked")
        Class<NoahServiceImpl<E, T>> serviceImplClass = (Class<NoahServiceImpl<E, T>>) this.generateClass(serviceImplMake, serviceImplPackageName);

        //注入bean
        NoahServiceImpl<E, T> instance = null;
        try {
            instance = serviceImplClass.newInstance();
            instance.setBaseMapper(e);
        } catch (InstantiationException | IllegalAccessException ex) {
            log.error("创建service实例失败：{}", ex.getMessage());
            ex.printStackTrace();
        }
        return instance;
    }

    /**
     * 生成class
     *
     * @param make        动态构建
     * @param packageName 包名
     * @return 生成类
     */
    private Class<?> generateClass(DynamicType.Unloaded<?> make, String packageName) {
        Class<?> tClass = null;
        String saveClassPath = this.getSaveClassPath();
        try {
            make.saveIn(new File(saveClassPath));
            tClass = ClassUtils.searchClassByClassName(saveClassPath, packageName, Thread.currentThread().getContextClassLoader().getParent());
        } catch (IOException | ClassNotFoundException | NoSuchMethodException e) {
            log.debug("生成baseMapper class文件失败：{}", e.getMessage());
            e.printStackTrace();
        }
        return tClass;
    }

    /**
     * 获取mapperName
     *
     * @param tClass 对象Class
     * @return mapper名
     */
    private String getMapperName(Class tClass) {
        return this.prefix.concat(tClass.getSimpleName()).concat("Mapper");
    }

    /**
     * 获取serviceName
     *
     * @param tClass 对象Class
     * @return service名
     */
    private String getServiceName(Class tClass) {
        String simpleName = tClass.getSimpleName();
        return this.prefix.concat(simpleName).concat("Service");
    }

    /**
     * 获取serviceName实现类名
     *
     * @param tClass 对象Class
     * @return serviceImpl 名字
     */
    private String getServiceImplName(Class tClass) {
        String simpleName = tClass.getSimpleName();
        return this.prefix.concat(simpleName).concat("ServiceImpl");
    }

    /**
     * 获取保存路径
     *
     * @return 绝对路径
     */
    @Override
    public String getSaveClassPath() {
        return ProjectPathConfig.PROJECT_PATH + JavassistBuilder.DIRECTORY_NAME;
    }
}