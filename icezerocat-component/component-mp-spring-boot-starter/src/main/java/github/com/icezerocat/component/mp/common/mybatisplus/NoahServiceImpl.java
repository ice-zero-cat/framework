package github.com.icezerocat.component.mp.common.mybatisplus;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import github.com.icezerocat.component.common.utils.StringUtil;
import github.com.icezerocat.component.mp.annotations.MultipleTableId;
import github.com.icezerocat.component.mp.common.enums.NoahSqlMethod;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Description: 重写ServiceImpl
 * CreateDate:  2020/8/8 12:13
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
public abstract class NoahServiceImpl<E extends BaseMapper<T>, T> extends ServiceImpl<E, T> implements IService<T> {

    @Resource
    protected E baseMapper;

    @Override
    public E getBaseMapper() {
        return this.baseMapper;
    }

    public void setBaseMapper(E e) {
        this.baseMapper = e;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {

        int i = 0;
        String sqlStatement = SqlHelper.table(currentModelClass()).getSqlStatement(NoahSqlMethod.INSERT_BATCH.getMethod());
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            for (T anEntityList : entityList) {
                batchSqlSession.insert(sqlStatement, anEntityList);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        }
        Class<?> cls = currentModelClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
        int i = 0;
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            for (T anEntityList : entityList) {
                //获取多主键
                TableCheck tableCheck = this.containsKey(cls, anEntityList, tableInfo);
                boolean isMultipleTableId = tableCheck.getKeyCount() > 1;
                boolean hasKeyProperty = (null != tableInfo && StringUtils.isNotBlank(tableInfo.getKeyProperty()));
                if (hasKeyProperty || isMultipleTableId) {
                    if (tableCheck.isContainsKey()) {
                        batchSqlSession.insert(SqlHelper.table(currentModelClass()).getSqlStatement(NoahSqlMethod.INSERT_BATCH.getMethod()), anEntityList);
                    } else {
                        MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                        param.put(Constants.ENTITY, anEntityList);

                        //判断是否有复合主键
                        if (isMultipleTableId) {
                            param.put(Constants.WRAPPER, (T) tableCheck.getQuery());
                            batchSqlSession.update(sqlStatement(SqlMethod.UPDATE), param);
                        } else {
                            batchSqlSession.update(sqlStatement(SqlMethod.UPDATE_BY_ID), param);
                        }

                    }
                    //不知道以后会不会有人说更新失败了还要执行插入
                    if (i >= 1 && i % batchSize == 0) {
                        batchSqlSession.flushStatements();
                    }
                    i++;
                } else {
                    throw ExceptionUtils.mpe("Error:  Can not execute. Could not find @TableId.");
                }
                batchSqlSession.flushStatements();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw ExceptionUtils.mpe("Error:  Unable to resolve @TableId. Entity may be multiple @TableId or does not exist @TableId");
        }
        return true;
    }

    /**
     * 检查主键是否存在
     *
     * @param cls       类
     * @param entity    对象
     * @param tableInfo 表单信息
     * @return 判断是否为空，需要插入（空为true，需要插入）
     * @throws IllegalAccessException 非法访问异常
     */
    private TableCheck containsKey(Class<?> cls, T entity, TableInfo tableInfo) throws IllegalAccessException {
        TableCheck tableCheck = new TableCheck();
        Map<String, Object> idsMap = new HashMap<>();
        Field[] declaredFields = cls.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            MultipleTableId tableIdAnn = field.getAnnotation(MultipleTableId.class);
            if (tableIdAnn != null) {
                String name = org.apache.commons.lang3.StringUtils.isNotBlank(tableIdAnn.value()) ?
                        tableIdAnn.value() : StringUtil.camel2Underline(field.getName());
                idsMap.put(name, field.get(entity));
            }
        }
        tableCheck.setKeyCount(idsMap.size());
        if (idsMap.size() <= 1) {
            Object idVal = ReflectionKit.getFieldValue(entity, tableInfo.getKeyProperty());
            boolean result = StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal));
            tableCheck.setContainsKey(result);
            return tableCheck;
        } else {
            QueryWrapper<T> query = Wrappers.query();
            boolean isNullBl = true;
            for (Map.Entry<String, Object> entry : idsMap.entrySet()) {
                query.eq(entry.getKey(), entry.getValue());
                if (isNullBl) {
                    isNullBl = StringUtils.checkValNull(entry.getValue());
                }
            }
            List<T> tList = getBaseMapper().selectList(query);
            boolean emptyT = CollectionUtils.isEmpty(tList);
            boolean result = isNullBl || emptyT;
            tableCheck.setContainsKey(result);
            tableCheck.setQuery(query);
            return tableCheck;
        }
    }

    /**
     * 表单检查类
     */
    @Data
    private class TableCheck {
        /**
         * 更具主键判断数据是否存在（true：存在）
         */
        private boolean containsKey;

        /**
         * 主键总数
         */
        private int keyCount;

        /**
         * 查询条件
         */
        private QueryWrapper<T> query = Wrappers.query();
    }
}
