package github.com.icezerocat.jdbctemplate.service;

import com.sun.istack.NotNull;
import github.com.icezerocat.core.model.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcOperations;

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
     * @param tClass 类字节码
     * @return 插入结果
     */
    int[] insert(Class<?> tClass);

    /**
     * 插入list集合对象
     *
     * @param listT list集合
     * @param <T>   泛型
     * @return 插入结果
     */
    <T> int[] insert(List<T> listT);
}
