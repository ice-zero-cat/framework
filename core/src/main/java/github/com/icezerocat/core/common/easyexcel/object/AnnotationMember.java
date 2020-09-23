package github.com.icezerocat.core.common.easyexcel.object;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 注解成员
 * CreateDate:  2020/7/20 19:39
 *
 * @author zero
 * @version 1.0
 */
@Data
public class AnnotationMember implements Serializable {
    /**
     * 成员
     */
    private String member = "value";
    /**
     * 类型
     */
    private String type = "string";
    /**
     * 值
     */
    private Object value;

    /**
     * 数组值
     */
    private List<Object> valueList;
}
