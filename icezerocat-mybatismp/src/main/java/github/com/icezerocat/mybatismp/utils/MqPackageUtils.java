package github.com.icezerocat.mybatismp.utils;

import github.com.icezerocat.mybatismp.base.BaseMpEntity;
import org.reflections.Reflections;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * Description: 类工具类
 * CreateDate:  2020/11/19 13:07
 *
 * @author zero
 * @version 1.0
 */
public class MqPackageUtils {

    private static Reflections reflections;

    /**
     * 获取项目包名
     *
     * @return 项目包名
     */
    public static String getProjectPackName() {
        Package pack = MqPackageUtils.class.getPackage();
        String packName = pack.getName();
        do {
            packName = packName.substring(0, packName.lastIndexOf("."));
            pack = Package.getPackage(packName);
        } while (null != pack);
        return packName;
    }

    /**
     * 获取mq实体类
     *
     * @param entityName 实体类简称
     * @return 实体类
     */
    public static Class<?> getMqClassByName(String entityName) {
        entityName = StringUtils.capitalize(entityName);
        if (reflections == null) {
            reflections = new Reflections(getProjectPackName());
        }
        Set<Class<? extends BaseMpEntity>> classBySuper = reflections.getSubTypesOf(BaseMpEntity.class);
        if (classBySuper != null) {
            for (Class<?> c : classBySuper) {
                String simpleName = c.getSimpleName();
                if (simpleName.equalsIgnoreCase(entityName)) {
                    return c;
                }

            }
        }
        return null;
    }

    /**
     * 通过名字获取对象
     *
     * @param entityName 对象名
     * @return 对象
     * @throws IllegalAccessException 非法访问异常
     * @throws InstantiationException 实力化异常
     */
    public static Object getEntityByName(String entityName) throws IllegalAccessException, InstantiationException {
        Class<?> aClass = getMqClassByName(entityName);
        if (aClass == null) {
            throw new NoClassDefFoundError("项目没有此对象".concat(entityName));
        }
        return aClass.newInstance();
    }
}
