package github.com.icezerocat.component.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esotericsoftware.reflectasm.MethodAccess;
import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.common.utils.DateUtil;
import github.com.icezerocat.component.common.utils.ReflectAsmUtil;
import github.com.icezerocat.component.core.exception.ApiException;
import github.com.icezerocat.component.mp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.component.mp.config.MpApplicationContextHelper;
import github.com.icezerocat.component.mp.model.MpModel;
import github.com.icezerocat.component.mp.model.MpResult;
import github.com.icezerocat.component.mp.model.Search;
import github.com.icezerocat.component.mp.service.MpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: mp服务
 * CreateDate:  2021/5/11 21:43
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@Service("mpService")
@SuppressWarnings("unused")
public class MpServiceImpl implements MpService {

    @Override
    public <M> MpResult<M> invoke(MpModel mpModel) {
        Object service = this.getService(mpModel);
        if (service == null) {
            throw new ApiException("Service获取失败");
        }
        MethodAccess methodAccess = ReflectAsmUtil.get(service.getClass());
        try {
            @SuppressWarnings("all")
            M m = (M) methodAccess.invoke(service, mpModel.getServiceMethod(), mpModel);
            return MpResult.getInstance(m);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                cause.printStackTrace();
                throw new ApiException(cause.getMessage());
            }
            if (e instanceof InvocationTargetException) {
                Throwable t = ((InvocationTargetException) e).getTargetException();
                if (t != null) {
                    t.printStackTrace();
                    throw new ApiException(t.getMessage());
                }
            }
            e.printStackTrace();
            throw new ApiException(e.getMessage());
        }
    }

    @Override
    public HttpResult<List<?>> retrieve(NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService, MpModel mpModel) {
        Wrapper<Object> wrapper = this.getWrapper(mpModel);
        HttpResult.Build<List<?>> build = HttpResult.Build.getInstance();
        if (wrapper == null) {
            return HttpResult.error("搜索条件出错");
        }
        if (mpModel.getPage() > -1 && mpModel.getLimit() > -1) {
            Page<Object> ipage = new Page<>(mpModel.getPage(), mpModel.getLimit());
            Page selectPage = baseMapperObjectNoahService.getBaseMapper().selectPage(ipage, wrapper);
            return build.setCode(HttpStatus.OK.value()).setCount(selectPage.getTotal()).setData(selectPage.getRecords()).complete();
        } else {
            List selectList = baseMapperObjectNoahService.getBaseMapper().selectList(wrapper);
            return build.setCode(HttpStatus.OK.value()).setCount(selectList.size()).setData(selectList).complete();
        }
    }

    @Override
    public List<Object> saveOrUpdateBatch(NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService, List<Map<String, Object>> mapList, Class aClass) {
        String entityName = aClass.getName();
        List<Object> objectList = new ArrayList<>();
        try {
            List<Object> objects = new ArrayList<>();
            for (Map<String, Object> map : mapList) {
                Object newInstance = aClass.newInstance();
                ReflectAsmUtil.mapToBean(map, newInstance);
                objects.add(newInstance);
            }
            boolean isSave = baseMapperObjectNoahService.saveOrUpdateBatch(objects);
            if (isSave) {
                objectList = objects;
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new ApiException("创建对象失败:" + entityName);
        }
        return objectList;
    }

    @Override
    public <T> Wrapper<T> getWrapper(MpModel mpModel) {
        QueryWrapper<T> query = Wrappers.query();
        for (Search search : mpModel.getSearches()) {
            //判断是否是日期格式需要转换
            if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(search.getFormatDate())) {
                Date date = DateUtil.parse(String.valueOf(search.getValue()), search.getFormatDate());
                search.setValue(date);
            }
            //搜索条件默认类型：like，还是自定义类型：eq、ne等
            if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isBlank(search.getType())) {
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

    /**
     * 获取service
     *
     * @param mpModel mp模型
     * @return service
     */
    private Object getService(MpModel mpModel) {
        Object service = null;
        String serviceName = "";
        if (!StringUtils.isEmpty(mpModel.getServiceName())) {
            serviceName = mpModel.getServiceName();
        } else {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(mpModel.getApClassModelBuild().getTableName())) {
                serviceName = "mpTableService";
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(mpModel.getEntityName())) {
                serviceName = "mpEntityService";
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(mpModel.getBeanName())) {
                serviceName = "mpBeanService";
            }
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(serviceName)) {
            service = MpApplicationContextHelper.getBean(serviceName);
        }
        return service;
    }

}
