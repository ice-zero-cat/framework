package github.com.icezerocat.jdbctemplate.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 获取插入参数信息
 * CreateDate:  2021/12/31 16:29
 *
 * @author zero
 * @version 1.0
 */
@Data
@Builder
public class SqlDataModel implements Serializable {

    /**
     * 执行语句
     */
    private String executeSql;
    /**
     * value值（一个list代表一条数据，Object[]一个值对应一个参数）
     */
    private List<Object[]> valuesList;
    /**
     * 主键信息
     */
    private List<PrimaryKeyInfo> primaryKeyInfoList;
}
