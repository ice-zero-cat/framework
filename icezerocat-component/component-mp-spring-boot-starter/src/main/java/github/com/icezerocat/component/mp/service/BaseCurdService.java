package github.com.icezerocat.component.mp.service;


import com.baomidou.mybatisplus.core.metadata.OrderItem;
import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.mp.model.Search;

import java.util.List;
import java.util.Map;

/**
 * Description: 基础增删查改
 * CreateDate:  2020/8/7 9:55
 *
 * @author zero
 * @version 1.0
 */
public interface BaseCurdService {
    /**
     * 查询
     *
     * @param beanName bean名
     * @param page     页码
     * @param limit    每一页大小
     * @param searches 搜索条件
     * @return 查询结果
     */
    HttpResult retrieve(String beanName, long page, long limit, List<Search> searches);

    /**
     * 查询
     *
     * @param beanName bean名
     * @param page     页码
     * @param limit    每一页大小
     * @param searches 搜索条件
     * @param orders   排序
     * @return 查询结果
     */
    HttpResult retrieve(String beanName, long page, long limit, List<Search> searches, List<OrderItem> orders);

    /**
     * 删除
     *
     * @param beanName bean名
     * @param ids      id集合
     * @return 删除结果 int
     */
    HttpResult delete(String beanName, List<Long> ids);

    /**
     * 批量创建对象
     *
     * @param beanName   bean 名
     * @param entityName 对像名
     * @param mapList    数据
     * @return 插入结果 boolean
     */
    HttpResult saveOrUpdateBatch(String beanName, String entityName, List<Map<String, Object>> mapList);

    /**
     * 批量保存或更新对象
     *
     * @param tableName  表单名
     * @param objectList 对象数据
     * @return 插入结构boolean
     */
    HttpResult saveOrUpdateBatchByTableName(String tableName, List<Map<String, Object>> objectList);

    /**
     * 查询
     *
     * @param tableName 表名
     * @param page      页码
     * @param limit     每一页大小
     * @param searches  搜索条件
     * @return 查询结果
     */
    HttpResult<List<?>> retrieveByTableName(String tableName, long page, long limit, List<Search> searches);

    /**
     * 查询
     *
     * @param tableName 表名
     * @return 查询结果
     */
    HttpResult<List<?>> retrieveAllByTableName(String tableName);

    /**
     * 查询
     *
     * @param tableName 表名
     * @param page      页码
     * @param limit     每一页大小
     * @param searches  搜索条件
     * @param orders    排序
     * @return 查询结果
     */
    HttpResult<List<?>> retrieveByTableName(String tableName, long page, long limit, List<Search> searches, List<OrderItem> orders);

    /**
     * 根据已有对象进行查询
     *
     * @param entityName 对象名
     * @param page       页码
     * @param limit      页大小
     * @param searches   搜索条件
     * @return 查询结果
     */
    HttpResult<List<?>> retrieveByEntity(String entityName, long page, long limit, List<Search> searches);

    /**
     * 根据已有对象进行查询全部数据
     *
     * @param entityName 对象名
     * @return 查询结果
     */
    HttpResult<List<?>> retrieveAllByEntity(String entityName);

    /**
     * 根据已有对象进行查询
     *
     * @param entityName 对象名
     * @param page       页码
     * @param limit      页大小
     * @param searches   搜索条件
     * @param orders     排序
     * @return 查询结果
     */
    HttpResult<List<?>> retrieveByEntity(String entityName, long page, long limit, List<Search> searches, List<OrderItem> orders);

    /**
     * 删除
     *
     * @param tableName 表名
     * @param ids       id集合
     * @return 删除结果 int
     */
    HttpResult deleteByTableName(String tableName, List<Long> ids);

    /**
     * 删除根据搜索条件删除（无对象，根据表单名自动生成对象）
     *
     * @param tableName 表名
     * @param searches  搜索条件
     * @return 删除结果 int
     */
    HttpResult deleteBySearch(String tableName, List<Search> searches);

    /**
     * 删除根据已有对象，搜索删除（自定义对象）
     *
     * @param entity   bean名
     * @param searches 搜索条件
     * @return 删除结果 int
     */
    HttpResult deleteByEntitySearch(String entity, List<Search> searches);

    /**
     * 批量保存或更新对象
     *
     * @param entityName 对象名称（自定义复杂的对象-非自动生成）
     * @param mapList    对象数据据（list）
     * @return 保存结果
     */
    HttpResult saveOrUpdateBatch(String entityName, List<Map<String, Object>> mapList);
}
