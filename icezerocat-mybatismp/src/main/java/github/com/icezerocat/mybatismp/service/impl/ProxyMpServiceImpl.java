package github.com.icezerocat.mybatismp.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.com.icezerocat.core.common.easyexcel.object.builder.JavassistBuilder;
import github.com.icezerocat.core.config.ZeroWebConfig;
import github.com.icezerocat.mybatismp.service.ProxyMpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.PatternMatchUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * Description: 代理mp服务实现类
 * CreateDate:  2020/9/14 10:18
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@Service("proxyMpService")
public class ProxyMpServiceImpl implements ProxyMpService {

    @Resource
    private MapperScannerConfigurer mapperScannerConfigurer;
    @Resource
    private SqlSessionFactory sqlSessionFactory;
    private final DefaultListableBeanFactory defaultListableBeanFactory;
    private final SqlSessionTemplate sqlSessionTemplate;

    @Nullable
    private MetadataReaderFactory metadataReaderFactory;
    @Nullable
    private ResourcePatternResolver resourcePatternResolver;
    @Nullable
    private Environment environment;
    private BeanDefinitionDefaults beanDefinitionDefaults = new BeanDefinitionDefaults();
    @Nullable
    private String[] autowireCandidatePatterns;
    private BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;
    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private boolean lazyInitialization;

    public ProxyMpServiceImpl(SqlSessionTemplate sqlSessionTemplate, DefaultListableBeanFactory defaultListableBeanFactory) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Override
    public <T extends BaseMapper> T proxy(Class<T> tClass) throws Exception {
        MapperFactoryBean<T> mapperFactoryBean = new MapperFactoryBean<>(tClass);

        //方法一: 重新扫描mapper包注入
        //注册Mapper Bean到容器中.(BeanDefinition的class设置为MapperFactoryBean)
        //this.mapperScannerConfigurer.postProcessBeanDefinitionRegistry(this.defaultListableBeanFactory);

        //方法二: 优化注入单个mapper
        this.processBeanDefinitions(this.doScan(tClass.getName()), mapperFactoryBean);

        //将mapperInterface注册到configuration中。
        this.sqlSessionTemplate.getConfiguration().addMapper(tClass);

        //MapperProxy代理生成，通过SqlSessionTemplate去获取我们得Mapper代理。
        mapperFactoryBean.setSqlSessionTemplate(this.sqlSessionTemplate);
        return mapperFactoryBean.getObject();
    }

    /**
     * 扫描包名获取bean定义类
     *
     * @param packageSearchPath 包名路径
     * @return bean定义
     */
    private BeanDefinitionHolder doScan(String packageSearchPath) {
        BeanDefinitionHolder beanDefinitionHolder = null;
        // 通过查找候选bean定义
        BeanDefinition candidate = this.scanCandidateComponents(packageSearchPath);
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
        // 设置作用域
        candidate.setScope(scopeMetadata.getScopeName());
        // 生成beanName
        String beanName = this.beanNameGenerator.generateBeanName(candidate, this.defaultListableBeanFactory);

        if (candidate instanceof AbstractBeanDefinition) {
            // 增加默认值，autowireCandidate
            postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
        }
        if (candidate instanceof AnnotatedBeanDefinition) {
            AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
        }

        // 注册BeanDefinition到容器中。
        if (checkCandidate(beanName, candidate)) {
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
            definitionHolder =
                    this.applyScopedProxyMode(scopeMetadata, definitionHolder, this.defaultListableBeanFactory);
            beanDefinitionHolder = definitionHolder;
            registerBeanDefinition(definitionHolder, this.defaultListableBeanFactory);
        }
        return beanDefinitionHolder;
    }

    /**
     * 处理Mapper的bean定义类
     *
     * @param holder bean定义类
     */
    private void processBeanDefinitions(BeanDefinitionHolder holder, MapperFactoryBean mapperFactoryBean) {
        GenericBeanDefinition definition;
        definition = (GenericBeanDefinition) holder.getBeanDefinition();
        String beanClassName = definition.getBeanClassName();

        // the mapper interface is the original class of the bean
        // but, the actual class of the bean is MapperFactoryBean
        // issue #59
        // 构造器参数，下一行代码将Bean设置为MapperFactoryBean，MapperFactoryBean的构造器中有个参数是mapperInterface
        assert beanClassName != null;
        definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
        // 这一步非常重要，把我们的Bean设置为MapperFactoryBean,接下来会看到MapperFactoryBean的继承关系
        definition.setBeanClass(mapperFactoryBean.getClass());

        definition.getPropertyValues().add("addToConfig", true);

        boolean explicitFactoryUsed = false;

        // 在bean中增加sqlSessionFactory
        if (this.sqlSessionFactory != null) {
            definition.getPropertyValues().add("sqlSessionFactory", this.sqlSessionFactory);
            explicitFactoryUsed = true;
        }

        // 在bean中增加sqlSessionTemplate
        if (this.sqlSessionTemplate != null) {
            definition.getPropertyValues().add("sqlSessionTemplate", this.sqlSessionTemplate);
            explicitFactoryUsed = true;
        }

        // 通过类型为名称为MapperFactoryBean启用自动装配
        if (!explicitFactoryUsed) {
            log.debug("Enabling autowire by type for MapperFactoryBean with name '" + holder.getBeanName() + "'.");
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        }
        definition.setLazyInit(this.lazyInitialization);
    }

    private BeanDefinitionHolder applyScopedProxyMode(
            ScopeMetadata metadata, BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {

        ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
        if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
            return definition;
        }
        boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
        return this.createScopedProxy(definition, registry, proxyTargetClass);
    }

    private BeanDefinitionHolder createScopedProxy(
            BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry, boolean proxyTargetClass) {

        return ScopedProxyUtils.createScopedProxy(definitionHolder, registry, proxyTargetClass);
    }

    /**
     * Register the specified bean with the given registry.
     * <p>Can be overridden in subclasses, e.g. to adapt the registration
     * process or to register further bean definitions for each scanned bean.
     *
     * @param definitionHolder the bean definition plus bean name for the bean
     * @param registry         the BeanDefinitionRegistry to register the bean with
     */
    private void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

    /**
     * Check the given candidate's bean name, determining whether the corresponding
     * bean definition needs to be registered or conflicts with an existing definition.
     *
     * @param beanName       the suggested name for the bean
     * @param beanDefinition the corresponding bean definition
     * @return {@code true} if the bean can be registered as-is;
     * {@code false} if it should be skipped because there is an
     * existing, compatible bean definition for the specified name
     * @throws IllegalStateException if an existing, incompatible
     *                               bean definition has been found for the specified name
     */
    private boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        if (!this.defaultListableBeanFactory.containsBeanDefinition(beanName)) {
            return true;
        }
        BeanDefinition existingDef = this.defaultListableBeanFactory.getBeanDefinition(beanName);
        BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
        if (originatingDef != null) {
            existingDef = originatingDef;
        }
        if (isCompatible(beanDefinition, existingDef)) {
            return false;
        }
        throw new IllegalStateException("Annotation-specified bean name '" + beanName +
                "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
                "non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
    }

    /**
     * Determine whether the given new bean definition is compatible with
     * the given existing bean definition.
     * <p>The default implementation considers them as compatible when the existing
     * bean definition comes from the same source or from a non-scanning source.
     *
     * @param newDefinition      the new bean definition, originated from scanning
     * @param existingDefinition the existing bean definition, potentially an
     *                           explicitly defined one or a previously generated one from scanning
     * @return whether the definitions are considered as compatible, with the
     * new definition to be skipped in favor of the existing definition
     */
    private boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
        // 显式注册覆盖的bean

        //扫面通用bean
        boolean scannedBeanBl = existingDefinition instanceof ScannedGenericBeanDefinition;
        // 扫描相同文件两次
        boolean scannedFileBl = newDefinition.getSource() != null && newDefinition.getSource().equals(existingDefinition.getSource());
        // 扫描等效文件两次
        boolean scannedEquivalentClass = newDefinition.equals(existingDefinition);

        log.debug("扫面通用bean:{},扫描相同文件两次:{},扫描等效文件两次:{}", scannedBeanBl, scannedFileBl, scannedEquivalentClass);
        return (!scannedBeanBl || scannedFileBl || scannedEquivalentClass);
    }

    /**
     * Scan the class path for candidate components.
     *
     * @param packageSearchPath the package to check for annotated classes
     * @return autodetected bean definitions
     */
    private BeanDefinition scanCandidateComponents(String packageSearchPath) {
        BeanDefinition beanDefinition = null;
        org.springframework.core.io.Resource[] resources;
        try {
            //自定义系统资源路径
            packageSearchPath = packageSearchPath.replaceAll("\\.", "/");
            String path = ZeroWebConfig.PROJECT_PATH + JavassistBuilder.DIRECTORY_NAME + File.separator + packageSearchPath + ".class";
            resources = getResourcePatternResolver().getResources(path);
            for (org.springframework.core.io.Resource resource : resources) {
                MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                sbd.setResource(resource);
                return sbd;
            }
        } catch (IOException e) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", e);
        }
        return beanDefinition;
    }

    private Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        return this.environment;
    }

    /**
     * Resolve the specified base package into a pattern specification for
     * the package search path.
     * <p>The default implementation resolves placeholders against system properties,
     * and converts a "."-based package path to a "/"-based resource path.
     *
     * @param basePackage the base package as specified by the user
     * @return the pattern specification to be used for package searching
     */
    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    /**
     * Return the MetadataReaderFactory used by this component provider.
     */
    private MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }

    /**
     * Return the ResourceLoader that this component provider uses.
     */
    private ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            ResourceLoader resourceLoader = new FileSystemResourceLoader();
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
        }
        return this.resourcePatternResolver;
    }

    public void setLazyInitialization(boolean lazyInitialization) {
        this.lazyInitialization = lazyInitialization;
    }

    /**
     * Apply further settings to the given bean definition,
     * beyond the contents retrieved from scanning the component class.
     *
     * @param beanDefinition the scanned bean definition
     * @param beanName       the generated bean name for the given bean
     */
    private void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
        beanDefinition.applyDefaults(this.beanDefinitionDefaults);
        if (this.autowireCandidatePatterns != null) {
            beanDefinition.setAutowireCandidate(PatternMatchUtils.simpleMatch(this.autowireCandidatePatterns, beanName));
        }
    }

    /**
     * Set the name-matching patterns for determining autowire candidates.
     *
     * @param autowireCandidatePatterns the patterns to match against
     */
    public void setAutowireCandidatePatterns(@Nullable String... autowireCandidatePatterns) {
        this.autowireCandidatePatterns = autowireCandidatePatterns;
    }
}
