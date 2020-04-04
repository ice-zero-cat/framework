package github.com.icezerocat.admin.jdbctemplate.service.impl;

import com.sun.istack.NotNull;
import github.com.icezerocat.admin.jdbctemplate.service.BaseJdbcTemplate;
import github.com.icezerocat.core.builder.SearchBuild;
import github.com.icezerocat.core.model.Search;
import github.com.icezerocat.core.utils.DaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ProjectName: [framework]
 * Package:     [github.com.icezerocat.admin.jdbcTemplate.Impl.BaseJdbcTemplate]
 * Description: jdbcTemplate扩展类
 * CreateDate:  2020/4/4 18:54
 *
 * @author 0.0.0
 * @version 1.0
 */
@Component
public class BaseJdbcTemplateImpl extends JdbcTemplate implements BaseJdbcTemplate {
    public BaseJdbcTemplateImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long delete(Class<?> tClass, @NotNull Long id) {
        String sql = "delete from " + DaoUtil.getTableName(tClass) + " where id = ?";
        return this.update(sql, id);
    }

    @Override
    public long delete(Class<?> tClass, Iterable<Long> ids) {
        StringBuilder stringBuilder = new StringBuilder();
        ids.forEach(id -> stringBuilder.append(id).append(","));
        String id = stringBuilder.substring(0, stringBuilder.length());
        String sql = "delete from " + DaoUtil.getTableName(tClass) + " where id in (?)";
        return this.update(sql, id);
    }

    @Override
    public <T> T findById(Class<T> tClass, @NotNull Long id) {
        return this.queryForObject("select * from " + DaoUtil.getTableName(tClass) + " where id = " + id, tClass);
    }

    @Override
    public <T> List<T> findById(Class<T> tClass, Iterable<Long> ids) {
        StringBuilder stringBuilder = new StringBuilder();
        ids.forEach(id -> stringBuilder.append(id).append(","));
        String id = stringBuilder.substring(0, stringBuilder.length());
        return this.query("select * from " + DaoUtil.getTableName(tClass) + " where id in (?)", new Object[]{id}, new BeanPropertyRowMapper<>(tClass));
    }

    @Override
    public <T> List<T> findAll(String sqlStr, Object[] objects, Class<T> entityClass, Pageable pageable) {
        return executePageable(sqlStr, objects, entityClass, pageable);
    }

    @Override
    public <T> List<T> findAll(String sqlStr, Object[] objects, Class<T> tClass) {
        return this.query(sqlStr, objects, new BeanPropertyRowMapper<>(tClass));
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        return this.query("select * from " + DaoUtil.getTableName(tClass), new Object[]{}, new BeanPropertyRowMapper<>(tClass));
    }

    @Override
    public long count(Class tClass) {
        Long count = this.queryForObject("select count(*) from " + DaoUtil.getTableName(tClass), new Object[]{}, Long.class);
        return count == null ? 0L : count;
    }

    @Override
    public long count(List<Search> searchList, Class<?> entityClass) {
        SearchBuild searchBuild = new SearchBuild.Builder(DaoUtil.getTableName(entityClass)).searchList(searchList).start();
        String sql = " select count(*) " + searchBuild.getHql();
        Long count = this.queryForObject(sql, searchBuild.getList(), Long.class);
        return count == null ? 0L : count;
    }

    @Override
    public <T> List<T> getList(List<Search> searchList, Class<T> entityClass, Pageable pageable) {
        SearchBuild searchBuild = new SearchBuild.Builder(DaoUtil.getTableName(entityClass)).searchList(searchList).start();
        String sql = "SELECT * " + searchBuild.getHql();
        return executePageable(sql, searchBuild.getList(), entityClass, pageable);
    }

    /**
     * 解析pageable后执行sql
     *
     * @param sqlStr      sql语句
     * @param objects     查询值
     * @param entityClass 返回对象
     * @param pageable    分页排序
     * @return 结果集
     */
    private <T> List<T> executePageable(String sqlStr, Object[] objects, Class<T> entityClass, Pageable pageable) {

        StringBuilder sqlBuild = new StringBuilder(sqlStr);

        //是否有select，没有则添加
        if (!sqlStr.trim().toUpperCase().startsWith("SELECT")) {
            sqlBuild.insert(0, " SELECT ");
        }

        //排序语句拼接
        if (pageable != null && !pageable.getSort().isEmpty()) {
            sqlBuild.append(" ORDER BY ");
            String sortStr = pageable.getSort().toString();
            String[] sortArr = sortStr.split(",");
            for (String sort : sortArr) {
                String[] sortObj = sort.split(":");
                sqlBuild.append(sortObj[0]).append(" ").append(sortObj[1]).append(",");
            }
            sqlBuild.replace(sqlBuild.length() - 1, sqlBuild.length(), " ");
        }

        List<Object> list = new ArrayList<>(Arrays.asList(objects));
        //分页语句拼接(分页大小等于1，取全部数据)
        if (pageable != null && pageable.getPageSize() != 1) {
            sqlBuild.append(" LIMIT ?,? ");
            list.add((pageable.getPageNumber() - 1) * pageable.getPageSize());
            list.add(pageable.getPageSize());
        }

        return this.query(sqlBuild.toString(), list.toArray(), new BeanPropertyRowMapper<>(entityClass));
    }
}
