package github.com.icezerocat.jdbctemplate.model;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Description:查询sql模型
 * CreateDate:  2022/1/2 18:13
 *
 * @author zero
 * @version 1.0
 */
@ToString
@NoArgsConstructor
public class SelectSqlModel extends AbstractSqlModel {

    final private String START_SELECT_SQL = " SELECT ";
    final private String FROM_SELECT_SQL = " FROM ";

    /**
     * 表名构造函数
     *
     * @param tableName 表名
     */
    public SelectSqlModel(String tableName) {
        this.setTableName(tableName);
    }

    /**
     * 初始化数据构造
     *
     * @param tableName    表单名
     * @param filedBuilder 字段
     */
    public SelectSqlModel(String tableName, StringBuilder filedBuilder) {
        this.setTableName(tableName);
        this.filedBuilder = filedBuilder;
    }

    /**
     * 添加字段
     * <code>
     * filed = ?
     * </code>
     *
     * @param filed 字段
     * @return 当前操作类
     */
    @Override
    public SelectSqlModel addFiled(String filed) {
        this.filedBuilder.append(filed).append(this.SYMBOL_QUESTION_MARK);
        return this;
    }

    /**
     * 完成
     * <code>
     * select [filed] from [tableName]
     * </code>
     *
     * @return sql
     */
    @Override
    public String complete() {
        return this.START_SELECT_SQL + this.getFiledString() + this.FROM_SELECT_SQL + this.getTableName();
    }

}
