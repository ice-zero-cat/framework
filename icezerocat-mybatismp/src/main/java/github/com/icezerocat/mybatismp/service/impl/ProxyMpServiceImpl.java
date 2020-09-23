package github.com.icezerocat.mybatismp.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.com.icezerocat.mybatismp.service.ProxyMpService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import javax.annotation.Resource;
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
    private final DefaultListableBeanFactory defaultListableBeanFactory;
    private final SqlSessionTemplate sqlSessionTemplate;

    @Nullable
    private MetadataReaderFactory metadataReaderFactory;
    @Nullable
    private ResourcePatternResolver resourcePatternResolver;
    @Nullable
    private Environment environment;
    private BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;
    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
    private Class<? extends MapperFactoryBean> mapperFactoryBeanClass = MapperFactoryBean.class;
    private boolean lazyInitialization;

    public ProxyMpServiceImpl(SqlSessionTemplate sqlSessionTemplate, DefaultListableBeanFactory defaultListableBeanFactory) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Override
    public <T extends BaseMapper> T proxy(Class<T> tClass) throws Exception {
        //注册Mapper Bean到容器中.(BeanDefinition的class设置为MapperFactoryBean)
        this.mapperScannerConfigurer.postProcessBeanDefinitionRegistry(this.defaultListableBeanFactory);
        //TODO 优化注入单个mapper
        //this.processBeanDefinitions(this.doScan(tClass.getName()));

        //将类型注册到我们Mapper容器
        this.sqlSessionTemplate.getConfiguration().addMapper(tClass);

        //MapperProxy代理生成
        MapperFactoryBean<T> mapperFactoryBean = new MapperFactoryBean<>(tClass);
        mapperFactoryBean.setSqlSessionTemplate(this.sqlSessionTemplate);
        return mapperFactoryBean.getObject();
    }

    private BeanDefinitionHolder doScan(String packageSearchPath) {
        BeanDefinitionHolder beanDefinitionHolder = null;
        BeanDefinition candidate = this.scanCandidateComponents(packageSearchPath);
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
        candidate.setScope(scopeMetadata.getScopeName());
        String beanName = this.beanNameGenerator.generateBeanName(candidate, this.defaultListableBeanFactory);

        if (checkCandidate(beanName, candidate)) {
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
            definitionHolder =
                    this.applyScopedProxyMode(scopeMetadata, definitionHolder, this.defaultListableBeanFactory);
            beanDefinitionHolder = definitionHolder;
            registerBeanDefinition(definitionHolder, this.defaultListableBeanFactory);
        }
        return beanDefinitionHolder;
    }

    private void processBeanDefinitions(BeanDefinitionHolder holder) {
        GenericBeanDefinition definition;
        definition = (GenericBeanDefinition) holder.getBeanDefinition();
        String beanClassName = definition.getBeanClassName();

        // the mapper interface is the original class of the bean
        // but, the actual class of the bean is MapperFactoryBean
        definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName); // issue #59
        definition.setBeanClass(this.mapperFactoryBeanClass);

        definition.getPropertyValues().add("addToConfig", true);

        boolean explicitFactoryUsed = false;


        if (this.sqlSessionTemplate != null) {
            definition.getPropertyValues().add("sqlSessionTemplate", this.sqlSessionTemplate);
            explicitFactoryUsed = true;
        }

        if (!explicitFactoryUsed) {
            log.debug("Enabling autowire by type for MapperFactoryBean with name '" + holder.getBeanName() + "'.");
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        }
        definition.setLazyInit(lazyInitialization);
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
        return (!(existingDefinition instanceof ScannedGenericBeanDefinition) ||  // explicitly registered overriding bean
                (newDefinition.getSource() != null && newDefinition.getSource().equals(existingDefinition.getSource())) ||  // scanned same file twice
                newDefinition.equals(existingDefinition));  // scanned equivalent class twice
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
            resources = getResourcePatternResolver().getResources(packageSearchPath);
            for (org.springframework.core.io.Resource resource : resources) {
                MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(packageSearchPath);
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
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }
}
