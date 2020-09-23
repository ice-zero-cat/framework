package github.com.icezerocat.mybatismp.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.com.icezerocat.core.http.HttpResult;
import github.com.icezerocat.core.utils.DateUtil;
import github.com.icezerocat.mybatismp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.mybatismp.config.ApplicationContextHelper;
import github.com.icezerocat.mybatismp.model.Search;
import github.com.icezerocat.mybatismp.service.BaseCurdService;
import github.com.icezerocat.mybatismp.service.BaseMpBuildService;
import github.com.icezerocat.mybatismp.utils.PackageUtil;
import github.com.icezerocat.mybatismp.utils.ReflectAsmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 基础增删改查
 * CreateDate:  2020/8/7 9:55
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@Service
public class BaseCurdServiceImpl implements BaseCurdService {

    @Value("${entity.package}")
    private String traversePackage;

    private final BaseMpBuildService baseMpBuildService;

    private static Map<String, String> packageName;

    public BaseCurdServiceImpl(BaseMpBuildService baseMpBuildService) {
        this.baseMpBuildService = baseMpBuildService;
    }

    @Override
    public HttpResult retrieve(String beanName, long page, long limit, List<Search> searches) {
        Wrapper wrapper = this.getWrapper(searches);
        if (wrapper == null) {
            return HttpResult.error("搜索条件出错");
        }
        BaseMapper baseMapper = ApplicationContextHelper.getBean(ApplicationContextHelper.getBeanName(beanName), BaseMapper.class);
        if (page > -1 && limit > -1) {
            Page ipage = new Page(page, limit);
            return HttpResult.ok(baseMapper.selectPage(ipage, wrapper));
        } else {
            return HttpResult.ok(baseMapper.selectList(wrapper));
        }
    }

    @Override
    public HttpResult delete(String beanName, List<Long> ids) {
        BaseMapper baseMapper = ApplicationContextHelper.getBean(ApplicationContextHelper.getBeanName(beanName), BaseMapper.class);
        return HttpResult.ok(baseMapper.deleteBatchIds(ids));
    }

    @Override
    public HttpResult saveOrUpdateBatch(String beanName, String entityName, List<Map<String, Object>> mapList) {
        entityName = org.springframework.util.StringUtils.uncapitalize(entityName);
        beanName = ApplicationContextHelper.getBeanName(beanName);
        boolean insert = false;
        ServiceImpl service = ApplicationContextHelper.getBean(org.springframework.util.StringUtils.uncapitalize(beanName), ServiceImpl.class);
        try {
            if (packageName == null) {
                packageName = PackageUtil.getClassNameMap(traversePackage);
            }
            String fullPackageName = org.springframework.util.StringUtils.capitalize(entityName);
            if (!packageName.containsKey(fullPackageName)) {
                return HttpResult.error("找不到对象名【" + entityName + "】，请尝试全路径包名");
            }
            List<Object> objects = new ArrayList<>();
            for (Map<String, Object> map : mapList) {
                Class<?> aClass = Class.forName(packageName.get(fullPackageName));
                Object newInstance = aClass.newInstance();
                ReflectAsmUtil.mapToBean(map, newInstance);
                objects.add(newInstance);
            }
            insert = service.saveOrUpdateBatch(objects);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return HttpResult.error("找不到类");
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return HttpResult.ok(insert);
    }

    @Override
    public HttpResult saveOrUpdateBatch(String tableName, List<Object> objectList) {
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(tableName);
        return HttpResult.ok(baseMapperObjectNoahService.saveOrUpdateBatch(objectList));
    }

    /**
     * 获取mp搜索条件
     *
     * @param searches 搜索对象
     * @return mp搜索条件
     */
    private Wrapper getWrapper(List<Search> searches) {
        QueryWrapper<Object> query = Wrappers.query();
        for (Search search : searches) {
            //判断是否是日期格式需要转换
            if (StringUtils.isNotBlank(search.getFormatDate())) {
                Date date = DateUtil.parse(String.valueOf(search.getValue()), search.getFormatDate());
                search.setValue(date);
            }
            //搜索条件默认类型：like，还是自定义类型：eq、ne等
            if (StringUtils.isBlank(search.getType())) {
                query.like(search.getField(), search.getValue());
            } else {
                try {
                    Method[] declaredMethods = query.getClass().getSuperclass().getDeclaredMethods();
                    for (Method method : declaredMethods) {
                        if (method.getName().equals(search.getType())) {
                            method.setAccessible(true);
                            method.invoke(query, true, search.getField(), search.getValue());
                            break;
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("{}调用{}方法失败！\t{}", query.getClass().getName(), search.getType(), e.getMessage());
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        cause.printStackTrace();
                    }
                    if (e instanceof InvocationTargetException) {
                        Throwable t = ((InvocationTargetException) e).getTargetException();
                        if (t != null) {
                            t.printStackTrace();
                        }
                    }
                    e.printStackTrace();
                    return null;
                }

            }
        }
        return query;
    }


    /**
     * map转对象
     *
     * @param map   map
     * @param clazz 类
     * @return 对象
     */
    private Object map2Object(Map<String, Object> map, Class<?> clazz) {
        if (map == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = clazz.newInstance();

            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}