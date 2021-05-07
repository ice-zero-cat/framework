package github.com.icezerocat.component.mp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * Bean工厂
 * <p>
 * Created by zmj
 * On 2019/10/23.
 *
 * @author 0.0.0
 */
@Slf4j
@Component
public class MpApplicationContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        MpApplicationContextHelper.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        Object bean = null;
        if (applicationContext == null) {
            return null;
        }
        try {
            bean = applicationContext.getBean(name);
        } catch (BeansException ignored) {

        }
        return bean;
    }

    /**
     * 根据类获取Service层对象
     *
     * @param clazz 类
     * @param <T>   声明泛型类
     * @return 对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据name和class获取Service层对象
     *
     * @param beanName name
     * @param clazz    class
     * @param <T>      声明泛型类
     * @return 对象
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

    /**
     * 清空applicable持有者
     */
    public static void clearHolder() {
        applicationContext = null;
    }

    /**
     * 获取默认工厂
     *
     * @return DefaultListableBeanFactory
     */
    public static DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }

    /**
     * 注册bean
     *
     * @param beanName              bean
     * @param beanDefinitionBuilder bean定义构建者
     */
    public static void registerBeanDefinition(String beanName, BeanDefinitionBuilder beanDefinitionBuilder) {
        if (getBean(beanName) == null) {
            //单例
            beanDefinitionBuilder.setScope("prototype");
            getDefaultListableBeanFactory().registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        }
    }

    /**
     * 通过类注册bean
     * 自动生成beanName（MduSysmsgServiceImpl——> mduSysmsgService）
     *
     * @param tClass 注册类
     */
    /*public static void registerBeanDefinitionByClass(Class tClass) {
        String beanName = getBeanNameByClass(tClass);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(tClass);
        registerBeanDefinition(beanName, beanDefinitionBuilder);
    }*/

    /**
     * 通过类注册bean
     * 自动生成beanName（MduSysmsgServiceImpl—— mduSysmsgService）
     *
     * @param tClass 注册类
     * @param <T>    声明泛型类
     * @return bean
     */
    public static <T> T registerBeanDefinitionByClass(Class<T> tClass) {
        String beanName = getBeanNameByClass(tClass);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(tClass);
        registerBeanDefinition(beanName, beanDefinitionBuilder);
        return getBean(tClass);
    }

    /**
     * 通过类注册bean
     *
     * @param className 类名全称
     */
    public static void registerBeanDefinitionByClassName(String className) {
        String beanName = getBeanNameByClassName(className);
        Class<?> tClass;
        try {
            tClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("通过[{}]，无法创建类:{}", className, e.getMessage());
            e.printStackTrace();
            return;
        }
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(tClass);
        registerBeanDefinition(beanName, beanDefinitionBuilder);
    }

    /**
     * 删除bean
     *
     * @param beanName bean名字
     */
    public static void removeBeanDefinition(String beanName) {
        getDefaultListableBeanFactory().removeBeanDefinition(beanName);
    }

    /**
     * 获取bean名字
     *
     * @param serviceInterface 服务接口
     * @return bean名
     */
    public static String getBeanName(String serviceInterface) {
        String beanName = org.springframework.util.StringUtils.uncapitalize(serviceInterface);
        final int beanLength = 4;
        final String suffix = "IMPL";
        if (!org.springframework.util.StringUtils.isEmpty(beanName) && beanName.length() > beanLength && suffix.equals(beanName.substring(beanName.length() - beanLength).toUpperCase())) {
            beanName = beanName.substring(0, beanName.length() - 4);
        }
        return beanName;
    }

    /**
     * 获取bean名字通过类
     *
     * @param tClass bean类
     * @return bean名
     */
    public static String getBeanNameByClass(Class tClass) {
        return getBeanName(tClass.getSimpleName());
    }

    /**
     * 获取bean名字通过类名
     *
     * @param className 类名
     * @return bean名
     */
    public static String getBeanNameByClassName(String className) {
        return getBeanName(className.substring(className.lastIndexOf(".") + 1));
    }
}
