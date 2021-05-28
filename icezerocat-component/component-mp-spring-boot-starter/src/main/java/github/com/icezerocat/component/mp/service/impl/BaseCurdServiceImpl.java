package github.com.icezerocat.component.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.common.model.ApClassModel;
import github.com.icezerocat.component.common.utils.DateUtil;
import github.com.icezerocat.component.mp.model.MpModel;
import github.com.icezerocat.component.mp.model.Search;
import github.com.icezerocat.component.mp.service.BaseCurdService;
import github.com.icezerocat.component.mp.service.MpBeanService;
import github.com.icezerocat.component.mp.service.MpEntityService;
import github.com.icezerocat.component.mp.service.MpTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
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

    private final MpTableService mpTableService;
    private final MpEntityService mpEntityService;
    private final MpBeanService mpBeanService;

    public BaseCurdServiceImpl(MpTableService mpTableService, MpEntityService mpEntityService, MpBeanService mpBeanService) {
        this.mpTableService = mpTableService;
        this.mpEntityService = mpEntityService;
        this.mpBeanService = mpBeanService;
    }

    @Override
    public HttpResult retrieve(String beanName, long page, long limit, List<Search> searches) {
        return this.retrieve(beanName, page, limit, searches, Collections.emptyList());
    }

    @Override
    public HttpResult retrieve(String beanName, long page, long limit, List<Search> searches, List<OrderItem> orders) {
        return HttpResult.ok(this.mpBeanService.retrieve(MpModel.builder().beanName(beanName).page(page).limit(limit).searches(searches).orders(orders).build()));
    }

    @Override
    public HttpResult delete(String beanName, List<Long> ids) {
        return HttpResult.ok(this.mpBeanService.deleteByIds(MpModel.builder().beanName(beanName).ids(ids).build()));
    }

    @Override
    public HttpResult saveOrUpdateBatch(String beanName, String entityName, List<Map<String, Object>> mapList) {
        try {
            List<Object> objectList = this.mpBeanService.saveOrUpdateBatch(MpModel.builder().beanName(beanName).entityName(entityName).objectList(mapList).build());
            return CollectionUtils.isEmpty(objectList) ? HttpResult.error("保存失败") : HttpResult.ok(objectList);
        } catch (Exception e) {
            return HttpResult.error(e.getMessage());
        }
    }

    @Override
    public HttpResult saveOrUpdateBatchByTableName(String tableName, List<Map<String, Object>> objectList) {
        HttpResult.Build<Boolean> instance = HttpResult.Build.getInstance();
        boolean isInsert = false;
        try {
            List<Object> objects = this.mpTableService.saveOrUpdateBatch(MpModel.builder().apClassModelBuild(ApClassModel.Build.getInstance(tableName)).objectList(objectList).build());
            isInsert = CollectionUtils.isEmpty(objects);
        } catch (Exception e) {
            instance.setMsg(e.getMessage());
            instance.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        instance.setData(isInsert);
        return isInsert ? instance.setCode(HttpStatus.OK.value()).complete() : instance.complete();
    }

    @Override
    public HttpResult<List<?>> retrieveByTableName(String tableName, long page, long limit, List<Search> searches) {
        return this.retrieveByTableName(tableName, page, limit, searches, Collections.emptyList());
    }

    @Override
    public HttpResult<List<?>> retrieveAllByTableName(String tableName) {
        return this.retrieveByTableName(tableName, -1, -1, Collections.emptyList());
    }

    @Override
    public HttpResult<List<?>> retrieveByTableName(String tableName, long page, long limit, List<Search> searches, List<OrderItem> orders) {
        return this.mpTableService.retrieve(MpModel.builder()
                .apClassModelBuild(ApClassModel.Build.getInstance(tableName))
                .page(page)
                .limit(limit)
                .searches(searches)
                .orders(orders).
                        build());
    }

    @Override
    public HttpResult<List<?>> retrieveByEntity(String entityName, long page, long limit, List<Search> searches) {
        return this.retrieveByEntity(entityName, page, limit, searches, Collections.emptyList());
    }

    @Override
    public HttpResult<List<?>> retrieveAllByEntity(String entityName) {
        return this.retrieveByEntity(entityName, -1, -1, Collections.emptyList());
    }

    @Override
    public HttpResult<List<?>> retrieveByEntity(String entityName, long page, long limit, List<Search> searches, List<OrderItem> orders) {
        return this.mpEntityService.retrieve(MpModel.builder().entityName(entityName).page(page).limit(limit).searches(searches).orders(orders).build());
    }

    @Override
    public HttpResult deleteByTableName(String tableName, List<Long> ids) {
        return HttpResult.ok(this.mpTableService.deleteByIds(MpModel.builder().apClassModelBuild(ApClassModel.Build.getInstance(tableName)).ids(ids).build()));
    }

    @Override
    public HttpResult deleteBySearch(String tableName, List<Search> searches) {
        return HttpResult.ok(this.mpTableService.deleteBySearch(MpModel.builder().apClassModelBuild(ApClassModel.Build.getInstance(tableName)).searches(searches).build()));
    }

    @Override
    public HttpResult deleteByEntitySearch(String entity, List<Search> searches) {
        boolean isDelete = this.mpEntityService.deleteBySearch(MpModel.builder().entityName(entity).searches(searches).build());
        if (isDelete) {
            return HttpResult.ok("删除成功");
        } else {
            return HttpResult.error("删除失败或构建实体类存在异常！");
        }
    }

    @Override
    public HttpResult saveOrUpdateBatch(String entityName, List<Map<String, Object>> mapList) {
        return HttpResult.ok(this.mpEntityService.saveOrUpdateBatch(MpModel.builder().entityName(entityName).objectList(mapList).build()));
    }


    @Override
    public <T> Wrapper<T> getWrapper(MpModel mpModel) {
        QueryWrapper<T> query = Wrappers.query();
        for (Search search : mpModel.getSearches()) {
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
        if (!org.springframework.util.CollectionUtils.isEmpty(mpModel.getOrders())) {
            mpModel.getOrders().forEach(orderItem -> {
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
