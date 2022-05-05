package github.com.icezerocat.jdbctemplate.model;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Description: 更新sql模型
 * CreateDate:  2022/1/2 18:13
 *
 * @author zero
 * @version 1.0
 */
@ToString
@NoArgsConstructor
public class UpdateSqlModel extends AbstractSqlModel {

    final private String START_UPDATE_SQL = " UPDATE ";
    final private String SET_UPDATE_SQL = " SET ";

    /**
     * 表名构造函数
     *
     * @param tableName 表名
     */
    public UpdateSqlModel(String tableName) {
        this.setTableName(tableName);
    }

    /**
     * 初始化数据构造
     *
     * @param tableName    表单名
     * @param filedBuilder 字段
     */
    public UpdateSqlModel(String tableName, StringBuilder filedBuilder) {
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
    public UpdateSqlModel addFiled(String filed) {
        this.filedBuilder.append(filed).append(this.SYMBOL_EQUAL_SIGN).append(this.SYMBOL_QUESTION_MARK);
        return this;
    }

    /**
     * 完成
     * <code>
     * update [tableName] set
     * [filed = ?]
     * </code>
     *
     * @return sql
     */
    @Override
    public String complete() {
        return this.START_UPDATE_SQL + this.getTableName() + this.SET_UPDATE_SQL +
                //字段
                this.SYMBOL_LEFT_PARENTHESIS + this.getFiledString() + this.SYMBOL_RIGHT_PARENTHESIS;
    }

}
