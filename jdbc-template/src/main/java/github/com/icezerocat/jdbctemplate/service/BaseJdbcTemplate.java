package github.com.icezerocat.jdbctemplate.service;


import github.com.icezerocat.component.common.model.Param;
import github.com.icezerocat.jdbctemplate.service.impl.BaseJdbcTemplateImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcOperations;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * ProjectName: [framework]
 * Package:     [github.com.icezerocat.admin.jdbcTemplate.BaseJdbcTemplate]
 * Description: 扩展JdbcTemplate接口
 * CreateDate:  2020/4/4 18:54
 *
 * @author 0.0.0
 * @version 1.0
 */
@SuppressWarnings("all")
public interface BaseJdbcTemplate extends JdbcOperations {

    /**
     * 删除数据
     *
     * @param tClass 字节码
     * @param id     id
     * @return 返回删除多少条
     */
    long delete(Class<?> tClass, @NotNull Long id);

    /**
     * 删除表数据
     *
     * @param tClass 字节码
     * @return 删除结果
     */
    boolean deleteAll(Class<?> tClass);

    /**
     * 删除数据
     *
     * @param tClass 字节码
     * @param ids    id
     * @return 返回删除多少条
     */
    long delete(Class<?> tClass, Iterable<Long> ids);


    /**
     * 通过id获取数据
     *
     * @param tClass 字节码
     * @param id     id
     * @param <T>    泛型
     * @return 对象
     */
    <T> T findById(Class<T> tClass, @NotNull Long id);

    /**
     * 通过id获取数据
     *
     * @param tClass 字节码
     * @param ids    id列表
     * @param <T>    泛型
     * @return 对象
     */
    <T> List<T> findById(Class<T> tClass, Iterable<Long> ids);

    /**
     * 获取分页对象数据
     *
     * @param sqlStr      sql语句
     * @param objects     查询条件参数
     * @param entityClass 对象类
     * @param pageable    分页数据
     * @param <T>         泛型
     * @return list分页对象数据
     */
    <T> List<T> findAll(String sqlStr, Object[] objects, Class<T> entityClass, Pageable pageable);

    /**
     * 获取list对象
     *
     * @param sqlStr  select语句
     * @param objects 参数
     * @param tClass  实体类字节码
     * @param <T>     泛型
     * @return list对象
     */
    <T> List<T> findAll(String sqlStr, Object[] objects, Class<T> tClass);

    /**
     * 获取全部数据
     *
     * @param tClass 实体类字节码
     * @param <T>    泛型
     * @return list对象
     */
    <T> List<T> findAll(Class<T> tClass);

    /**
     * 统计总数
     *
     * @param tClass 实体类
     * @return 总数
     */
    long count(Class<?> tClass);

    /**
     * 统计总数
     *
     * @param searchList 搜索条件
     * @param tClass     实体类
     * @return 总数
     */
    long count(List<Param> searchList, Class<?> tClass);

    /**
     * 获取数据库对象数据
     *
     * @param searchList  查询条件
     * @param entityClass 对象类
     * @param pageable    排序分页
     * @param <T>         泛型
     * @return list分页数据
     */
    <T> List<T> getList(List<Param> searchList, Class<T> entityClass, Pageable pageable);

    /**
     * 插入对象
     *
     * @param t   对象
     * @param <T> 泛型
     * @return 插入结果
     */
    <T> int[] insert(T t);

    /**
     * 插入list集合对象
     *
     * @param listT list集合
     * @param <T>   泛型
     * @return 插入结果
     */
    <T> int[] insert(List<T> listT);

    /**
     * 批量保存或更新
     *
     * @param tCollection 对象集合
     * @param <T>         泛型对象
     * @return 保存和更新的总数
     */
    <T> int saveOrUpdateBatch(Collection<T> tCollection);

    /**
     * 获取更新数据的value
     *
     * @param t          实体类
     * @param tableCheck 检查表单主键对象
     * @param <T>        泛型
     * @return value数组
     */
    <T> Object[] getUpdateValue(T t, BaseJdbcTemplateImpl.TableCheck tableCheck);

    /**
     * 获取更新sql语句
     *
     * @param tClass     对象对应的字节码类
     * @param tableCheck 检查表单主键对象
     * @param <T>        泛型
     * @return insert-sql
     */
    <T> String getUpdateSql(Class<T> tClass, BaseJdbcTemplateImpl.TableCheck tableCheck);

    /**
     * 生成删除语句
     *
     * @param tClass     实体类
     * @param tableCheck 表单检查
     * @param <T>        泛型
     * @return 获取删除sql
     */
    <T> String getDeleteSql(Class<T> tClass, BaseJdbcTemplateImpl.TableCheck tableCheck);

    /**
     * 获取删除语句的value
     *
     * @param t          获取删除的值
     * @param tableCheck 表单检查
     * @param <T>        泛型
     * @return 获取删除value值
     */
    <T> Object[] getDeleteValue(T t, BaseJdbcTemplateImpl.TableCheck tableCheck);
}
