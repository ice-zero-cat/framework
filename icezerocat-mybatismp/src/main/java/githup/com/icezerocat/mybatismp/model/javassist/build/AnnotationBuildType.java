package githup.com.icezerocat.mybatismp.model.javassist.build;

/**
 * Description: 注解构建类型
 * CreateDate:  2020/7/24 14:56
 *
 * @author zero
 * @version 1.0
 */
@SuppressWarnings("unused")
public enum AnnotationBuildType {
    /**
     * 数据基本类型
     */
    STRING("string"),
    INTEGER("integer"),
    BOOLEAN("boolean"),
    SHORT("short"),
    ANNOTATION("annotation"),
    BYTE("byte"),
    CHAR("char"),
    DOUBLE("double"),
    CLASS("class");

    final private String value;

    AnnotationBuildType(String value) {
        this.value = value;
    }

    public final String getValue() {
        return this.value;
    }

    public final String getArrValue() {
        return this.value + "_arr";
    }
}
