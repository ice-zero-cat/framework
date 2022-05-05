package github.com.icezerocat.jdbctemplate.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Description: 主键信息
 * CreateDate:  2021/12/31 17:41
 *
 * @author zero
 * @version 1.0
 */
@Data
@Builder
public class PrimaryKeyInfo implements Serializable {

    /**
     * 实体类名字
     */
    private String name;
    /**
     * 主键值
     */
    private Object value;
    /**
     * 数据库名
     */
    private String dbName;
    /**
     * 字段
     */
    private Field field;
    /**
     * 主键类型
     */
    private String keyType;
}
