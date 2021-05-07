package github.com.icezerocat.component.common.easyexcel.object;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 字段注解
 * CreateDate:  2020/7/20 19:37
 *
 * @author zero
 * @version 1.0
 */
@Data
public class FieldAnnotation implements Serializable {
    /**
     * 注解类名
     */
    private String className;
    /**
     * 注解成员
     */
    private List<AnnotationMember> annotationMemberList;
}
