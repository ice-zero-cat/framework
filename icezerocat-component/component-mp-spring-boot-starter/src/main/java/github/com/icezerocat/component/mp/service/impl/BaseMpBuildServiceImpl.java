package github.com.icezerocat.component.mp.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import github.com.icezerocat.component.common.easyexcel.object.ExcelWriter;
import github.com.icezerocat.component.common.easyexcel.object.FieldAnnotation;
import github.com.icezerocat.component.common.model.ApClassModel;
import github.com.icezerocat.component.common.utils.ClassUtils;
import github.com.icezerocat.component.common.utils.StringUtil;
import github.com.icezerocat.component.core.config.ProjectPathConfig;
import github.com.icezerocat.component.db.builder.JavassistBuilder;
import github.com.icezerocat.component.db.service.ClassService;
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
import java.util.*;

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
     * 记录通过表明动态注入的service{表名：service}
     */
    private static Map<String, NoahServiceImpl<BaseMapper<Object>, Object>> tableNameToServiceByTableMap = new HashMap<>();
    /**
     * 记录通过对象动态注入的service{表名：service}
     */
    private static Map<String, Object> tableNameToServiceByEntityMap = new HashMap<>();
    /**
     * 缓存baseMapperClass{tableName: baseMapper}
     */
    private static Map<String, Class> tableNameToBaseMapperClassMap = new HashMap<>();
    /**
     * 表单是否需要重新加载（存在则不加载；不存在则加载）
     */
    private static Set<String> tableReloadSet = new HashSet<>();

    private final ProxyMpService proxyMpService;
    private final ClassService classService;

    private String prefix = "Ap";
    private String mapperPackage = JavassistBuilder.PACKAGE_NAME.concat("mapper.");
    private String mapperService = JavassistBuilder.PACKAGE_NAME.concat("service.");
    private String mapperServiceImpl = JavassistBuilder.PACKAGE_NAME.concat("service.impl.");

    public BaseMpBuildServiceImpl(ProxyMpService proxyMpService, ClassService classService) {
        this.proxyMpService = proxyMpService;
        this.classService = classService;
    }

    @Override
    public NoahServiceImpl<BaseMapper<Object>, Object> newInstance(ApClassModel.Build apClassModelBuild) {
        Map<String, ExcelWriter> excelWriterMap = apClassModelBuild.getExcelWriterMap();
        String tableName = apClassModelBuild.getTableName().toLowerCase();
        boolean isReloadTable = excelWriterMap != null && !excelWriterMap.isEmpty() && tableReloadSet.add(tableName);

        //判断是否需要重新记载表单
        if (tableNameToServiceByTableMap.containsKey(tableName) && isReloadTable) {
            boolean removeInstance = this.removeInstance(tableName);
            log.debug("isReloadTable:{}", removeInstance);
        }

        //没有jvm缓存service时：重新生成service
        if (!tableNameToServiceByTableMap.containsKey(tableName)) {
            Object o = this.generateEntity(apClassModelBuild);
            NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.newInstance(o);
            if (baseMapperObjectNoahService != null) {
                tableNameToServiceByTableMap.put(tableName, baseMapperObjectNoahService);
            }
        }
        return tableNameToServiceByTableMap.get(tableName);
    }

    @Override
    public NoahServiceImpl<BaseMapper<Object>, Object> newInstance(String tableName) {
        return this.newInstance(this.generateEntity(ApClassModel.Build.getInstance(tableName)));
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
        Class<?> tClass = t.getClass();
        TableName annotation = tClass.getAnnotation(TableName.class);
        String tableName = Optional.ofNullable(annotation).map(TableName::value).orElse(StringUtil.camel2Underline(tClass.getSimpleName()));
        tableName = tableName.toLowerCase();
        //jvm缓存是否存在
        if (!tableNameToServiceByEntityMap.containsKey(tableName)) {
            //判断class缓存是否已经初始化过
            String beanName = StringUtils.uncapitalize(this.getServiceName(t.getClass()));
            Object bean = MpApplicationContextHelper.getBean(beanName);
            if (bean != null) {
                tableNameToServiceByEntityMap.put(tableName, bean);
            } else {
                Class<BaseMapper<T>> baseMapperClass = this.generateMapper(t);
                try {
                    tableNameToBaseMapperClassMap.put(tableName, baseMapperClass);
                    BaseMapper<T> tBaseMapper = this.proxyMpService.proxy(baseMapperClass);
                    tableNameToServiceByEntityMap.put(tableName, this.generateService(t, baseMapperClass, tBaseMapper));
                } catch (Exception e) {
                    log.error("proxy NoahServiceImpl failed : {}", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        @SuppressWarnings("all")
        NoahServiceImpl<BaseMapper<T>, T> noahServiceImpl = (NoahServiceImpl<BaseMapper<T>, T>) tableNameToServiceByEntityMap.get(tableName);
        return noahServiceImpl;
    }

    @Override
    public boolean removeAllInstance() {
        for (Object o : tableNameToBaseMapperClassMap.values()) {
            @SuppressWarnings("all")
            Class<BaseMapper> baseMapperClass = (Class<BaseMapper>) o;
            boolean isRemoveProxy = this.proxyMpService.removeProxy(baseMapperClass);
            if (!isRemoveProxy) {
                return false;
            }
        }
        tableNameToServiceByTableMap.clear();
        tableNameToServiceByEntityMap.clear();
        tableReloadSet.clear();
        tableNameToBaseMapperClassMap.clear();
        return true;
    }

    @Override
    public boolean removeInstance(String tableName) {
        @SuppressWarnings("all")
        Class<BaseMapper> baseMapperClass = tableNameToBaseMapperClassMap.get(tableName);
        boolean isRemove = this.proxyMpService.removeProxy(baseMapperClass);
        if (isRemove) {
            tableNameToServiceByTableMap.remove(tableName);
            tableNameToServiceByEntityMap.remove(tableName);
            tableNameToBaseMapperClassMap.remove(tableName);
        }
        return isRemove;
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

    /**
     * 生成对象
     *
     * @param apClassModelBuild ap构建类
     * @return 对象
     */
    private Object generateEntity(ApClassModel.Build apClassModelBuild) {
        String tableName = apClassModelBuild.getTableName();
        String className = StringUtils.capitalize(StringUtil.underline2Camel(tableName));

        //添加表注解
        FieldAnnotation classAnnotation =
                FieldAnnotation.Build.getInstance(TableName.class.getName()).addAnnotationMember(tableName).complete();
        apClassModelBuild.setClassName(className).setClassAnnotationList(Collections.singletonList(classAnnotation));

        //字段注解
        Map<String, ExcelWriter> excelWriterMap = apClassModelBuild.getExcelWriterMap();
        excelWriterMap.put("id", ExcelWriter.Build.getInstance("id").addFieldAnnotation(TableId.class.getName()).complete());
        excelWriterMap.put("ID", ExcelWriter.Build.getInstance("ID").addFieldAnnotation(TableId.class.getName()).complete());

        //字段默认注解
        apClassModelBuild.setFieldDefaultAnnotationList(Collections.singletonList(FieldAnnotation.Build.getInstance(TableField.class.getName()).complete()));
        apClassModelBuild.setExcludeDefaultAnnotationFieldList(Arrays.asList("id", "ID"));

        Class aClass = this.classService.generateClass(apClassModelBuild.complete());
        Object o = null;
        try {
            o = aClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("generateEntity构建对象出错:{}", e.getMessage());
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
        log.debug("generateServiceClass:{}", servicePackageName);

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
        log.debug("generateServiceImplClass:{}", serviceImplPackageName);

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
        log.debug("generateClass:{}", packageName);
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
}