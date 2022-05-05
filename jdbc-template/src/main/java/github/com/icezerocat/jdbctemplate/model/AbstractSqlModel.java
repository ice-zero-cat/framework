package github.com.icezerocat.jdbctemplate.model;

import github.com.icezerocat.component.common.builder.SearchBuild;
import github.com.icezerocat.component.common.model.Param;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Description: 抽象Sql模型
 * CreateDate:  2022/1/2 18:12
 *
 * @author zero
 * @version 1.0
 */
public abstract class AbstractSqlModel implements Serializable {
    /**
     * 符号
     */
    final public String SYMBOL_LEFT_PARENTHESIS = " ( ";
    final public String SYMBOL_RIGHT_PARENTHESIS = " ) ";
    final public String SYMBOL_COMMA = " , ";
    final public String SYMBOL_QUESTION_MARK = " ? ";
    final public String SYMBOL_EQUAL_SIGN = " = ";

    /**
     * 插入模式
     *
     * @return sql模式
     */
    public static InsertSqlModel insertSqlModel() {
        return new InsertSqlModel();
    }

    /**
     * 更新模式
     *
     * @return sql模式
     */
    public static UpdateSqlModel updateSqlModel() {
        return new UpdateSqlModel();
    }

    /**
     * 删除模式
     *
     * @return sql模式
     */
    public static DeleteSqlModel deleteSqlModel() {
        return new DeleteSqlModel();
    }

    /**
     * 选择模式
     *
     * @return sql模式
     */
    public static SelectSqlModel selectSqlModel() {
        return new SelectSqlModel();
    }

    /**
     * 字段构建者
     */
    public StringBuilder filedBuilder = new StringBuilder();

    /**
     * 占位符构建者
     */
    public StringBuilder placeholderBuilder = new StringBuilder();
    /**
     * 搜索条件
     */
    public List<Param> searchList = new ArrayList<>();

    /**
     * 表单名
     */
    private String tableName;

    /**
     * 获取表名
     *
     * @return 表名
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名
     *
     * @param tableName 表名
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 添加搜搜
     *
     * @param param 搜索参数
     * @return this
     */
    public AbstractSqlModel addSearch(Param param) {
        this.searchList.add(param);
        return this;
    }

    /**
     * 获取搜索语句
     * <code>
     * 1 = 1 and filedName = [value]
     * </code>
     *
     * @return sql
     */
    public String getSearchSql() {
        return Optional.ofNullable(this.searchList).filter(o -> !CollectionUtils.isEmpty(o))
                .map(o -> "1 = 1" + SearchBuild.Builder.query().searchList(this.searchList).start().getHql())
                .orElse("");
    }

    /**
     * 获取字段字符串
     *
     * @return 字段字符串
     */
    public String getFiledString() {
        return this.filedBuilder.substring(0, filedBuilder.length() - 2);
    }

    /**
     * 获取占位符字符串
     *
     * @return 占位符字符串
     */
    public String getPlaceholderString() {
        return this.placeholderBuilder.substring(0, placeholderBuilder.length() - 2);
    }

    /**
     * 添加字段
     *
     * @param filed 字段
     */
    public abstract AbstractSqlModel addFiled(String filed);

    /**
     * 完成sql
     *
     * @return 完成可运行的sql
     */
    public abstract String complete();
}
