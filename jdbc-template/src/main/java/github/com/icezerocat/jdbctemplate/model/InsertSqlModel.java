package github.com.icezerocat.jdbctemplate.model;

import lombok.*;

/**
 * Description: 插入sql模型
 * CreateDate:  2022/1/2 18:13
 *
 * @author zero
 * @version 1.0
 */
@ToString
@NoArgsConstructor
public class InsertSqlModel extends AbstractSqlModel {

    final private String START_INSERT_SQL = " INSERT INTO ";
    final private String VALUE_INSERT_SQL = " VALUES ";

    /**
     * 表名构造函数
     *
     * @param tableName 表名
     */
    public InsertSqlModel(String tableName) {
        this.setTableName(tableName);
    }

    /**
     * 初始化数据构造
     *
     * @param tableName    表单名
     * @param filedBuilder 字段
     */
    public InsertSqlModel(String tableName, StringBuilder filedBuilder) {
        this.setTableName(tableName);
        this.filedBuilder = filedBuilder;
    }

    /**
     * 添加字段
     *
     * @param filed 字段
     */
    @Override
    public AbstractSqlModel addFiled(String filed) {
        this.filedBuilder.append(filed).append(this.SYMBOL_COMMA);
        this.placeholderBuilder.append(this.SYMBOL_QUESTION_MARK).append(this.SYMBOL_COMMA);
        return this;
    }

    /**
     * 完成
     *
     * <code>
     *     insert into [tableName]
     *     [filed1 , filed2]
     *     values
     *     ( ? , ? )
     * </code>
     *
     * @return sql
     */
    @Override
    public String complete() {
        return this.START_INSERT_SQL + this.getTableName() +
                //字段
                this.SYMBOL_LEFT_PARENTHESIS + this.getFiledString() + this.SYMBOL_RIGHT_PARENTHESIS +
                //values 语法
                this.VALUE_INSERT_SQL +
                //占位符号
                this.SYMBOL_LEFT_PARENTHESIS + this.getPlaceholderString() + this.SYMBOL_RIGHT_PARENTHESIS;
    }

}
