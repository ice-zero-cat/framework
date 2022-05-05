package github.com.icezerocat.jdbctemplate.model;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Description: 删除sql模型
 * CreateDate:  2022/1/2 18:13
 *
 * @author zero
 * @version 1.0
 */
@ToString
@NoArgsConstructor
public class DeleteSqlModel extends AbstractSqlModel {

    final private String START_DELETE_SQL = " DELETE ";
    final private String FROM_DELETE_SQL = " FROM ";

    /**
     * 表名构造函数
     *
     * @param tableName 表名
     */
    public DeleteSqlModel(String tableName) {
        this.setTableName(tableName);
    }

    /**
     * 初始化数据构造
     *
     * @param tableName    表单名
     * @param filedBuilder 字段
     */
    public DeleteSqlModel(String tableName, StringBuilder filedBuilder) {
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
    public DeleteSqlModel addFiled(String filed) {
        this.filedBuilder.append(filed).append(this.SYMBOL_QUESTION_MARK);
        return this;
    }

    /**
     * 完成
     * <code>
     *  DELETE from [tableName]
     * </code>
     *
     * @return sql
     */
    @Override
    public String complete() {
        return this.START_DELETE_SQL + this.FROM_DELETE_SQL + this.getTableName();
    }

}
