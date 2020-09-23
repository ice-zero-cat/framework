package githup.com.icezerocat.mybatismp.common.enums;

/**
 * Description: 将Jdbc3KeyGenerator替换为NoKeyGenerator(缺点是批量添加不能返回id，对于不需要返回id的场景适用)
 * CreateDate:  2020/8/8 12:03
 *
 * @author zero
 * @version 1.0
 */
public enum NoahSqlMethod {

    /**
     * 插入
     */
    INSERT_BATCH("insertBatch", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>"),

    ;


    private final String method;
    private final String desc;
    private final String sql;

    NoahSqlMethod(String method, String desc, String sql) {
        this.method = method;
        this.desc = desc;
        this.sql = sql;
    }

    public String getMethod() {
        return method;
    }

    public String getDesc() {
        return desc;
    }

    public String getSql() {
        return sql;
    }
}
