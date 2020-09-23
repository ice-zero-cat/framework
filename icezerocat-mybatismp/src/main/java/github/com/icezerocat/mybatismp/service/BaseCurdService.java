package github.com.icezerocat.mybatismp.service;


import github.com.icezerocat.core.http.HttpResult;
import github.com.icezerocat.mybatismp.model.Search;

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
    HttpResult saveOrUpdateBatch(String tableName, List<Object> objectList);
}
