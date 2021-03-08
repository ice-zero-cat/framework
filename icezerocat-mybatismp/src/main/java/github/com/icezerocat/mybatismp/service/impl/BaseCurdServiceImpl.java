package github.com.icezerocat.mybatismp.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.com.icezerocat.core.common.easyexcel.object.builder.JavassistBuilder;
import github.com.icezerocat.core.http.HttpResult;
import github.com.icezerocat.core.utils.DateUtil;
import github.com.icezerocat.core.utils.StringUtil;
import github.com.icezerocat.mybatismp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.mybatismp.config.ApplicationContextHelper;
import github.com.icezerocat.mybatismp.model.Search;
import github.com.icezerocat.mybatismp.service.BaseCurdService;
import github.com.icezerocat.mybatismp.service.BaseMpBuildService;
import github.com.icezerocat.mybatismp.utils.MqPackageUtils;
import github.com.icezerocat.mybatismp.utils.PackageUtil;
import github.com.icezerocat.mybatismp.utils.ReflectAsmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

    @Value("${entity.package:}")
    private String traversePackage;

    private final BaseMpBuildService baseMpBuildService;

    private static Map<String, String> packageName;

    public BaseCurdServiceImpl(BaseMpBuildService baseMpBuildService) {
        this.baseMpBuildService = baseMpBuildService;
    }

    @Override
    public HttpResult retrieve(String beanName, long page, long limit, List<Search> searches) {
        return this.retrieve(beanName, page, limit, searches, Collections.emptyList());
    }

    @Override
    public HttpResult retrieve(String beanName, long page, long limit, List<Search> searches, List<OrderItem> orders) {
        Wrapper wrapper = this.getWrapper(searches, orders);
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
            if (packageName == null && ObjectUtils.isNotEmpty(traversePackage)) {
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
    public HttpResult saveOrUpdateBatchByTableName(String tableName, List<Map<String, Object>> objectList) {
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(tableName);
        String entityName = JavassistBuilder.PACKAGE_NAME + org.springframework.util.StringUtils.capitalize(StringUtil.underlineToCamelCase(tableName));
        boolean insert;
        try {
            Class aClass = Class.forName(entityName);

            List<Object> objects = new ArrayList<>();
            for (Map<String, Object> map : objectList) {
                Object newInstance = aClass.newInstance();
                ReflectAsmUtil.mapToBean(map, newInstance);
                objects.add(newInstance);
            }
            insert = baseMapperObjectNoahService.saveOrUpdateBatch(objects);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return HttpResult.error("找不到类:" + entityName);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return HttpResult.error("创建对象失败:" + entityName);
        }
        return HttpResult.ok(insert);
    }

    @Override
    public HttpResult<List<?>> retrieveByTableName(String tableName, long page, long limit, List<Search> searches) {
        return this.retrieveByTableName(tableName, page, limit, searches, Collections.emptyList());
    }

    @Override
    public HttpResult<List<?>> retrieveByTableName(String tableName, long page, long limit, List<Search> searches, List<OrderItem> orders) {
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(tableName);
        return this.retrieve(baseMapperObjectNoahService, page, limit, searches, orders);
    }

    @Override
    public HttpResult<List<?>> retrieveByEntity(String entityName, long page, long limit, List<Search> searches) {
        return this.retrieveByEntity(entityName, page, limit, searches, Collections.emptyList());
    }

    @Override
    public HttpResult<List<?>> retrieveByEntity(String entityName, long page, long limit, List<Search> searches, List<OrderItem> orders) {
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService;
        try {
            baseMapperObjectNoahService = this.baseMpBuildService.newInstance(MqPackageUtils.getEntityByName(entityName));
        } catch (IllegalAccessException | InstantiationException e) {
            String message = "项目没有此对象".concat(entityName).concat(":").concat(e.getMessage());
            log.error(message);
            e.printStackTrace();
            return HttpResult.Build.<List<?>>getInstance()
                    .setData(Collections.emptyList())
                    .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMsg(message)
                    .setCount(0L)
                    .complete();
        }
        return this.retrieve(baseMapperObjectNoahService, page, limit, searches, orders);
    }

    @Override
    public HttpResult deleteByTableName(String tableName, List<Long> ids) {
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(tableName);
        return HttpResult.ok(baseMapperObjectNoahService.removeByIds(ids));
    }

    @Override
    public HttpResult deleteBySearch(String tableName, List<Search> searches) {
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(tableName);
        return this.deleteBySearch(baseMapperObjectNoahService, searches);
    }

    @Override
    public HttpResult deleteByEntitySearch(String entity, List<Search> searches) {
        try {
            NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService =
                    this.baseMpBuildService.newInstance(MqPackageUtils.getEntityByName(entity));
            return this.deleteBySearch(baseMapperObjectNoahService, searches);
        } catch (IllegalAccessException | InstantiationException e) {
            log.error("删除失败或构建实体类存在异常！具体原因：".concat(e.getMessage()));
            e.printStackTrace();
            return HttpResult.error("删除失败或构建实体类存在异常！具体原因：".concat(e.getMessage()));
        }
    }

    @Override
    public HttpResult saveOrUpdateBatch(String entityName, List<Map<String, Object>> mapList) {
        try {
            Class aClass = MqPackageUtils.getMqClassByName(entityName);
            if (aClass == null) {
                return HttpResult.error("项目没有此对象".concat(entityName));
            }
            NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(aClass.newInstance());
            List<Object> objects = new ArrayList<>();
            for (Map<String, Object> map : mapList) {
                Object newInstance = aClass.newInstance();
                ReflectAsmUtil.mapToBean(map, newInstance);
                objects.add(newInstance);
            }
            return HttpResult.ok(baseMapperObjectNoahService.saveOrUpdateBatch(objects));
        } catch (IllegalAccessException | InstantiationException e) {
            log.error("类创建实例出错：{}", e.getMessage());
            e.printStackTrace();
            return HttpResult.error("类创建实例出错：".concat(e.getMessage()));
        }
    }

    /**
     * 查询
     *
     * @param baseMapperObjectNoahService service
     * @param page                        页码
     * @param limit                       页码大小
     * @param searches                    搜索条件
     * @param orders                      排序
     * @return 查询结果
     */
    private HttpResult<List<?>> retrieve(NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService, long page, long limit, List<Search> searches, List<OrderItem> orders) {
        Wrapper wrapper = this.getWrapper(searches, orders);
        HttpResult.Build<List<?>> build = HttpResult.Build.getInstance();
        if (wrapper == null) {
            return HttpResult.error("搜索条件出错");
        }
        if (page > -1 && limit > -1) {
            Page ipage = new Page(page, limit);
            Page selectPage = baseMapperObjectNoahService.getBaseMapper().selectPage(ipage, wrapper);
            return build.setCode(HttpStatus.OK.value()).setCount(selectPage.getTotal()).setData(selectPage.getRecords()).complete();
        } else {
            List selectList = baseMapperObjectNoahService.getBaseMapper().selectList(wrapper);
            return build.setCode(HttpStatus.OK.value()).setCount(selectList.size()).setData(selectList).complete();
        }
    }

    /**
     * 根据搜索条件删除数据
     *
     * @param baseMapperObjectNoahService 删除service实现类
     * @param searches                    搜索条件
     * @return 搜索结果
     */
    private HttpResult deleteBySearch(NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService, List<Search> searches) {
        Wrapper wrapper = this.getWrapper(searches, Collections.emptyList());
        if (wrapper == null || CollectionUtils.isEmpty(searches)) {
            return HttpResult.error("搜索条件为空");
        }
        boolean remove = baseMapperObjectNoahService.remove(wrapper);
        if (remove) {
            return HttpResult.ok("删除成功");
        } else {
            return HttpResult.error("删除失败");
        }
    }

    /**
     * 获取mp搜索条件
     *
     * @param searches 搜索对象
     * @return mp搜索条件
     */
    private Wrapper getWrapper(List<Search> searches, List<OrderItem> orders) {
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

        //排序
        if (!org.springframework.util.CollectionUtils.isEmpty(orders)) {
            orders.forEach(orderItem -> {
                if (orderItem.isAsc()) {
                    query.orderByAsc(orderItem.getColumn());
                } else {
                    query.orderByDesc(orderItem.getColumn());
                }
            });
        }
        return query;
    }
}
