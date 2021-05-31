package github.com.icezerocat.component.common.easyexcel.object;

import github.com.icezerocat.component.common.easyexcel.object.builder.AnnotationBuildType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationMember implements Serializable {
    /**
     * 成员
     */
    private String member = "value";
    /**
     * 类型
     * {@link AnnotationBuildType}
     */
    private String type = AnnotationBuildType.STRING.getValue();
    /**
     * 值
     */
    private Object value;

    /**
     * 数组值
     */
    private List<Object> valueList;

}
