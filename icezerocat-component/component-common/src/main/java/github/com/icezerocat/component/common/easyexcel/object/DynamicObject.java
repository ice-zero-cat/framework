package github.com.icezerocat.component.common.easyexcel.object;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.beans.BeanMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 动态对象（用于构建对象生成，不支持对象注解生成）
 * <p>
 * Created by zmj
 * On 2019/12/25.
 *
 * @author 0.0.0
 */
@Slf4j
@SuppressWarnings("unused")
public class DynamicObject {
    private Object object;
    private BeanMap beanMap;

    /**
     * 对象生成器
     */
    private BeanGenerator beanGenerator;

    /**
     * 对象的<属性名, 属性名对应的类型>
     */
    private Map<String, Class> allProperty;


    /**
     * 给对象属性赋值
     *  
     *
     * @param property 属性名
     * @param value    值
     */
    public void setValue(String property, Object value) {
        beanMap.put(property, value);
    }

    private void setValue(Object object, Map<String, Class> property) {
        for (String propertyName : property.keySet()) {
            if (allProperty.containsKey(propertyName)) {
                Object propertyValue = getPropertyValueByName(object, propertyName);
                this.setValue(propertyName, propertyValue);
            }
        }
    }

    private void setValue(Map<String, Object> propertyValue) {
        for (Entry<String, Object> entry : propertyValue.entrySet()) {
            this.setValue(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 通过属性名获取属性值
     *  
     *
     * @param property 属性名
     * @return 值
     */
    public Object getValue(String property) {
        return beanMap.get(property);
    }

    /**
     * 获取该bean的实体
     *  
     *
     * @return bean的实体
     */
    public Object getObject() {
        return this.object;
    }

    public Map<String, Class> getAllProperty() {
        return allProperty;
    }


    /**
     * 生成对象
     *
     * @param propertyMap 属性 Map<String,Class><属性名/类型>
     * @return 对象
     */
    private Object generateObject(Map<String, Class> propertyMap) {
        if (null == beanGenerator) {
            beanGenerator = new BeanGenerator();
        }
        for (String key : propertyMap.keySet()) {
            beanGenerator.addProperty(key, propertyMap.get(key));
        }
        return beanGenerator.create();
    }

    /**
     * 添加属性名与属性值
     *  
     *
     * @param propertyType  属性名
     * @param propertyValue 属性值
     */
    public void addProperty(Map<String, Class> propertyType, Map<String, Object> propertyValue) {
        if (null == propertyType) {
            throw new RuntimeException("动态添加属性失败！");
        }
        Object oldObject = object;
        object = generateObject(propertyType);
        beanMap = BeanMap.create(object);

        if (null != oldObject) {
            setValue(oldObject, allProperty);
        }

        setValue(propertyValue);
        if (null == allProperty) {
            allProperty = propertyType;
        } else {
            allProperty.putAll(propertyType);
        }
    }

    /**
     * 获取对象中的所有属性名 与属性值
     *
     * @param object 对象
     * @return 所有属性名与属性值
     * @throws ClassNotFoundException 找不到类异常
     */
    public Map<String, Class> getAllPropertyType(Object object) throws ClassNotFoundException {
        Map<String, Class> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            String propertyName = field.getName();
            Class<?> propertyType = Class.forName(field.getGenericType().getTypeName());
            map.put(propertyName, propertyType);
        }
        return map;
    }

    /**
     * 获取对象中的所有属性名与属性值
     *  
     *
     * @param object 对象
     * @return 所有属性名与属性值
     */
    public Map<String, Object> getAllPropertyValue(Object object) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            String propertyName = field.getName();
            Object propertyValue = getPropertyValueByName(object, propertyName);
            map.put(propertyName, propertyValue);
        }
        return map;
    }

    /**
     * 根据属性名获取对象中的属性值
     *  
     *
     * @param propertyName 对象
     * @param object       属性名
     * @return 属性值
     */
    private Object getPropertyValueByName(Object object, String propertyName) {
        String methodName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Object value = null;
        try {
            Method method = object.getClass().getMethod(methodName);
            value = method.invoke(object);
        } catch (Exception e) {
            log.error(String.format("从对象%s获取%s的=属性值失败", object, propertyName));
        }
        return value;
    }
}