package github.com.icezerocat.jdbctemplate.service.impl;

import github.com.icezerocat.component.common.builder.SearchBuild;
import github.com.icezerocat.component.common.model.Param;
import github.com.icezerocat.jdbctemplate.annotations.MultipleTableId;
import github.com.icezerocat.jdbctemplate.annotations.TableField;
import github.com.icezerocat.jdbctemplate.annotations.TableId;
import github.com.icezerocat.jdbctemplate.model.AbstractSqlModel;
import github.com.icezerocat.jdbctemplate.model.PrimaryKeyInfo;
import github.com.icezerocat.jdbctemplate.model.SqlDataModel;
import github.com.icezerocat.jdbctemplate.service.BaseJdbcTemplate;
import github.com.icezerocat.jdbctemplate.utils.DaoUtil;
import github.com.icezerocat.jdbctemplate.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

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
public class BaseJdbcTemplateImpl extends JdbcTemplate implements BaseJdbcTemplate {
    @SuppressWarnings("all")
    public BaseJdbcTemplateImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long delete(Class<?> tClass, @NotNull Long id) {
        String sql = "delete from " + DaoUtil.getTableName(tClass) + " where id = ?";
        return this.update(sql, id);
    }

    @Override
    public boolean deleteAll(Class<?> tClass) {
        String sql = "delete from " + DaoUtil.getTableName(tClass);
        int i = this.update(sql);
        return i >= 0;
    }

    @Override
    public long delete(Class<?> tClass, Iterable<Long> ids) {
        String id = String.valueOf(ids);
        id = id.substring(1, id.length() - 1);
        String sql = "delete from " + DaoUtil.getTableName(tClass) + " where id in ( ? )";
        return this.update(sql, id);
    }

    @Override
    public <T> T findById(Class<T> tClass, @NotNull Long id) {
        return this.queryForObject("select * from " + DaoUtil.getTableName(tClass) + " where id = ?", new BeanPropertyRowMapper<>(tClass), id);
    }

    @Override
    public <T> List<T> findById(Class<T> tClass, Iterable<Long> ids) {
        String id = String.valueOf(ids);
        id = id.substring(1, id.length() - 1);
        // 请查看示例 https://gist.github.com/retanoj/5fd369524a18ab68a4fe7ac5e0d121e8
        return this.query("select * from " + DaoUtil.getTableName(tClass) + " where id in ( ? )", new BeanPropertyRowMapper<>(tClass), id);
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
    public <T> int[] insert(T t) {
        List<T> tList = new ArrayList<>();
        tList.add(t);
        return this.insert(tList);
    }

    @Override
    public <T> int[] insert(List<T> listT) {
        if (CollectionUtils.isEmpty(listT)) {
            return new int[]{-2};
        }
        SqlDataModel sqlDataModel = this.constructSqlInfo(listT, AbstractSqlModel.insertSqlModel(), null);
        return this.batchUpdate(sqlDataModel.getExecuteSql(), sqlDataModel.getValuesList());
    }

    /**
     * 获取插入信息
     *
     * @param tList            数据集合
     * @param abstractSqlModel sql模型
     * @param consumer         字段消费者
     * @param <T>              泛型
     * @return 插入信息
     */
    private <T> SqlDataModel constructSqlInfo(List<T> tList, AbstractSqlModel abstractSqlModel, Consumer<Object> consumer) {

        Optional.of(tList).filter(o -> !CollectionUtils.isEmpty(o)).orElseThrow(() -> new RuntimeException("插入对象集合不能为空"));
        Class<?> tClass = tList.get(0).getClass();

        //sql收集器
        abstractSqlModel.setTableName(DaoUtil.getTableName(tClass));

        //value收集器
        List<Object[]> valuesList = new ArrayList<>();

        //字段
        LinkedHashSet<Field> fieldSet = new LinkedHashSet<>();

        //主键信息
        List<PrimaryKeyInfo> primaryKeyInfoList = new ArrayList<>();

        //获取执行的sql
        String executeSql = null;

        Field[] fields = tClass.getDeclaredFields();
        for (int i = 0; i < tList.size(); i++) {
            T t = tList.get(i);
            //value收集器
            List<Object> objectList = new ArrayList<>();
            if (i == 0) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    //忽略static、Ignore、Transient、id
                    String fieldName = field.getName();
                    boolean isStatic = Modifier.isStatic(field.getModifiers());
                    TableField tableField = field.getAnnotation(TableField.class);
                    boolean existBl = tableField == null || tableField.exist();
                    //获取包含的主键
                    PrimaryKeyInfo primaryKeyInfo = this.containsPrimaryKey(field, t);

                    //是单主键跳过，多主键时直接插入数据
                    if (primaryKeyInfo != null) {
                        primaryKeyInfoList.add(primaryKeyInfo);
                        if (TableId.class.getTypeName().equals(primaryKeyInfo.getKeyType())) {
                            continue;
                        }
                    }

                    //非静态变量和存在的字段操作，拼接sql
                    if (!isStatic && existBl) {
                        //获取字段名
                        String finalFieldName = fieldName;
                        fieldName = Optional.ofNullable(tableField)
                                .filter(tf -> org.apache.commons.lang3.StringUtils.isNotBlank(tf.value())).map(TableField::value)
                                .orElseGet(() -> StringUtil.camel2Underline(finalFieldName));

                        //sql拼接
                        abstractSqlModel.addFiled(fieldName);
                        try {
                            fieldSet.add(field);
                            Object tFieldValue = field.get(t);
                            objectList.add(tFieldValue);
                            //消费者处理字段
                            Optional.ofNullable(consumer).ifPresent(objectConsumer -> objectConsumer.accept(tFieldValue));
                        } catch (IllegalAccessException e) {
                            log.error("无法获取字段{}的值：{}", fieldName, e.getMessage(), e);
                            throw new RuntimeException(e);
                        }
                    }
                }
                //获取执行的sql
                executeSql = abstractSqlModel.complete();
            } else {
                fieldSet.forEach(field -> {
                    try {
                        Object tFieldValue = field.get(t);
                        objectList.add(tFieldValue);
                        //消费者
                        Optional.ofNullable(consumer).ifPresent(objectConsumer -> objectConsumer.accept(tFieldValue));
                    } catch (IllegalAccessException e) {
                        log.error("无法获取字段{}的值：{}", field.getName(), e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                });
            }

            //添加参数
            valuesList.add(objectList.toArray());
        }
        return SqlDataModel.builder().executeSql(executeSql).valuesList(valuesList).primaryKeyInfoList(primaryKeyInfoList).build();
    }

    /**
     * 获取包含的主键
     *
     * @param field 字段
     * @param t     对象
     * @return 主键信息
     */
    private <T> PrimaryKeyInfo containsPrimaryKey(Field field, T t) {
        PrimaryKeyInfo primaryKeyInfo = PrimaryKeyInfo.builder().build();

        String fieldName = field.getName();
        TableId tableId = field.getAnnotation(TableId.class);
        MultipleTableId multipleTableId = field.getAnnotation(MultipleTableId.class);
        Optional.empty().filter(o -> tableId != null && multipleTableId != null).ifPresent(o -> {
            throw new RuntimeException("TableId and MultipleTableId cannot coexist");
        });
        Annotation annotation = tableId == null ? multipleTableId : tableId;
        String dbName = null;
        String keyType = null;
        if (tableId != null) {
            dbName = tableId.value();
            keyType = TableId.class.getTypeName();
        }
        if (multipleTableId != null) {
            dbName = multipleTableId.value();
            keyType = MultipleTableId.class.getTypeName();
        }
        //判断是否存在主键
        boolean exitsPrimaryKeyBl = Objects.equals("ID", fieldName.toUpperCase()) || (annotation != null);
        if (exitsPrimaryKeyBl) {
            primaryKeyInfo = PrimaryKeyInfo.builder().build();
            primaryKeyInfo.setName(fieldName);
            primaryKeyInfo.setField(field);
            primaryKeyInfo.setKeyType(keyType);
            dbName = org.apache.commons.lang3.StringUtils.isNotBlank(dbName) ?
                    dbName : StringUtil.camel2Underline(field.getName());
            primaryKeyInfo.setDbName(dbName);
            Object value;
            try {
                value = field.get(t);
                primaryKeyInfo.setValue(value);
            } catch (IllegalAccessException e) {
                log.error("The primary key parsing errors：{}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return primaryKeyInfo;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T> int saveOrUpdateBatch(Collection<T> tCollection) {
        List<T> insertList = new ArrayList<T>();
        List<T> updateList = new ArrayList<T>();
        List<Object[]> updateValueList = new ArrayList<>();
        TableCheck tableCheck = null;
        for (T t : tCollection) {
            // TODO 主键获取
            tableCheck = null;//this.containsKey(t.getClass(), t);
            if (!tableCheck.isContainsKey()) {
                insertList.add(t);
            } else {
                updateValueList.add(this.getUpdateValue(t, tableCheck));
                updateList.add(t);
            }
        }

        //操作总数
        int count = 0;
        //插入总数
        if (!CollectionUtils.isEmpty(insertList)) {
            int[] insert = this.insert(insertList);
            int insertCount = insert != null && insert.length > 0 ? insert.length : 0;
            count += insertCount;
        }

        //更新总数
        if (!CollectionUtils.isEmpty(updateValueList)) {
            int[] batchUpdate = this.batchUpdate(this.getUpdateSql(updateList.get(0).getClass(), tableCheck), updateValueList);
            int updateCount = Math.max(batchUpdate.length, 0);
            count += updateCount;
        }

        return count;
    }

    @Override
    public <T> Object[] getUpdateValue(T t, TableCheck tableCheck) {
        return new Object[0];
    }

    @Override
    public <T> String getUpdateSql(Class<T> tClass, TableCheck tableCheck) {
        return null;
    }

    //TODO 根据主键更新
    public <T> Object[] updateByPk(List<T> tList) {
        SqlDataModel sqlDataModel = this.constructSqlInfo(tList, AbstractSqlModel.updateSqlModel(), null);
//        this.update()
        return null;
    }

    //TODO 根据逐渐查询语句
    public <T> String getSelectSql(Class<T> tClass, TableCheck tableCheck) {
        StringBuilder sql = new StringBuilder().append("SELECT").append(" * ").append(" FROM ").append(DaoUtil.getTableName(tClass));
        int index = 0;
        Map<String, Object> idsMap = tableCheck.getIdsMap();
        for (String key : tableCheck.getIdsMap().keySet()) {
            if (index == 0) {
                sql.append(" WHERE ").append(key).append(" = ").append(" ? ");
            } else {
                sql.append(" AND ").append(key).append(" = ").append(" ? ");
            }
            index++;
        }
        return sql.toString();
    }

    //生成删除语句
    @Override
    public <T> String getDeleteSql(Class<T> tClass, TableCheck tableCheck) {
        StringBuilder sql = new StringBuilder().append("DELETE").append(" FROM ").append(DaoUtil.getTableName(tClass));
        int index = 0;
        for (String key : tableCheck.getIdsMap().keySet()) {
            if (index == 0) {
                sql.append(" WHERE ").append(key).append(" = ").append(" ? ");
            } else {
                sql.append(" AND ").append(key).append(" = ").append(" ? ");
            }
            index++;
        }
        return sql.toString();
    }

    //获取删除语句的value
    @Override
    public <T> Object[] getDeleteValue(T t, TableCheck tableCheck) {
        List<Object> objectList = new ArrayList<>();
        //添加主键搜索条件ID字段
        for (Map.Entry<String, Object> map : tableCheck.getIdsMap().entrySet()) {
            objectList.add(map.getValue());
        }
        return objectList.toArray();
    }

    /**
     * 插入并获取主键
     */
    public <T> int insertAndGetId(T t) {
        /*final String sql = this.getInsertSql(t.getClass());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            this.getInsertValue(t, v -> ps.setObject());
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).intValue();*/
        return -1;
    }

    /**
     * 表单检查类
     */
    public static class TableCheck implements Serializable {

        public TableCheck() {
        }

        /**
         * 更具主键判断数据是否存在（true：存在）
         */
        private boolean containsKey = true;

        /**
         * 主键总数
         */
        private int keyCount;

        /**
         * 主键字段
         */
        private Map<String, Object> idsMap = new HashMap<>();

        public boolean isContainsKey() {
            return containsKey;
        }

        public void setContainsKey(boolean containsKey) {
            this.containsKey = containsKey;
        }

        public int getKeyCount() {
            return keyCount;
        }

        public void setKeyCount(int keyCount) {
            this.keyCount = keyCount;
        }

        public Map<String, Object> getIdsMap() {
            return idsMap;
        }

        public void setIdsMap(Map<String, Object> idsMap) {
            this.idsMap = idsMap;
        }

        @Override
        public String toString() {
            return "TableCheck{" +
                    "containsKey=" + containsKey +
                    ", keyCount=" + keyCount +
                    ", idsMap=" + idsMap +
                    '}';
        }
    }
}
