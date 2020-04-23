package github.com.icezerocat.jdbctemplate.service.impl;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.sun.istack.NotNull;
import github.com.icezerocat.core.builder.SearchBuild;
import github.com.icezerocat.core.model.Param;
import github.com.icezerocat.core.utils.DaoUtil;
import github.com.icezerocat.core.utils.ReflectAsmUtil;
import github.com.icezerocat.core.utils.StringUtil;
import github.com.icezerocat.jdbctemplate.service.BaseJdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.Transient;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * ProjectName: [framework]
 * Package:     [github.com.icezerocat.admin.jdbcTemplate.Impl.BaseJdbcTemplate]
 * Description: jdbcTemplate扩展类
 * CreateDate:  2020/4/4 18:54
 *
 * @author 0.0.0
 * @version 1.0
 */
@Slf4j
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
        String id = String.valueOf(ids);
        id = id.substring(1, id.length() - 1);
        String sql = "delete from " + DaoUtil.getTableName(tClass) + " where id in (" + id + ")";
        return this.update(sql, id);
    }

    @Override
    public <T> T findById(Class<T> tClass, @NotNull Long id) {
        return this.queryForObject("select * from " + DaoUtil.getTableName(tClass) + " where id = " + id, new BeanPropertyRowMapper<>(tClass));
    }

    @Override
    public <T> List<T> findById(Class<T> tClass, Iterable<Long> ids) {
        String id = String.valueOf(ids);
        id = id.substring(1, id.length() - 1);
        return this.query("select * from " + DaoUtil.getTableName(tClass) + " where id in (" + id + ")", new BeanPropertyRowMapper<>(tClass));
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
    public long count(List<Param> searchList, Class<?> entityClass) {
        SearchBuild searchBuild = new SearchBuild.Builder(DaoUtil.getTableName(entityClass)).searchList(searchList).start();
        String sql = " select count(*) " + searchBuild.getHql();
        Long count = this.queryForObject(sql, searchBuild.getList(), Long.class);
        return count == null ? 0L : count;
    }

    @Override
    public <T> List<T> getList(List<Param> searchList, Class<T> entityClass, Pageable pageable) {
        SearchBuild searchBuild = new SearchBuild.Builder(DaoUtil.getTableName(entityClass)).searchList(searchList).start();
        String sql = "SELECT * " + searchBuild.getHql();
        return executePageable(sql, searchBuild.getList(), entityClass, pageable);
    }

    @Override
    public int[] insert(Class<?> tClass) {
        List<Object[]> list = new ArrayList<>();
        list.add(getInsertValue(tClass));
        return this.batchUpdate(getInsertSql(tClass), list);
    }

    @Override
    public <T> int[] insert(List<T> listT) {
        List<Object[]> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(listT)) {
            return new int[]{-2};
        }
        listT.forEach(t -> list.add(getInsertValue(t)));
        return this.batchUpdate(getInsertSql(listT.get(0).getClass()), list);
    }

    /**
     * 获取插入数据的value
     *
     * @param t   实体类
     * @param <T> 泛型
     * @return value数组
     */
    private <T> Object[] getInsertValue(T t) {
        Class tClass = t.getClass();
        List<Object> objectList = new ArrayList<>();
        MethodAccess methodAccess = ReflectAsmUtil.get(tClass);
        Field[] fields = tClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            //忽略static、Ignore、Transient、id
            String fieldName = field.getName();
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            boolean annotationPresent = field.isAnnotationPresent(Transient.class);
            boolean id = Objects.equals("ID", fieldName.toUpperCase());
            if (!isStatic && !annotationPresent && !id) {
                //获取方法名
                String methodName;
                if (field.getType() == boolean.class) {
                    methodName = fieldName.contains("is") ? fieldName : "is" + StringUtils.capitalize(fieldName);
                } else {
                    methodName = "get" + StringUtils.capitalize(fieldName);
                }
                objectList.add(methodAccess.invoke(t, methodName));
            }
        }
        return objectList.toArray();
    }

    /**
     * 获取插入sql语句
     *
     * @param tClass 类字节码
     * @return insert-sql
     */
    private String getInsertSql(Class<?> tClass) {
        StringBuilder sql = new StringBuilder().append(" INSERT INTO ").append(DaoUtil.getTableName(tClass)).append(" ( ");
        StringBuilder o = new StringBuilder();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            //忽略static、Ignore、Transient、id
            String fieldName = field.getName();
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            boolean annotationPresent = field.isAnnotationPresent(Transient.class);
            boolean id = Objects.equals("ID", fieldName.toUpperCase());
            if (!isStatic && !annotationPresent && !id) {
                sql.append(StringUtil.camel2Underline(fieldName)).append(" , ");
                o.append(" ? ").append(" , ");
            }
        }
        sql.delete(sql.length() - 2, sql.length());
        o.delete(o.length() - 2, o.length());
        sql.append(" ) ").append(" VALUES ").append(" ( ").append(o.toString()).append(" ) ");
        return sql.toString();
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
        final String select = "SELECT";
        if (!sqlStr.trim().toUpperCase().startsWith(select)) {
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
