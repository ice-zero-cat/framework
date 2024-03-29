package github.com.icezerocat.component.db.builder;

import com.google.common.primitives.Ints;
import github.com.icezerocat.component.common.easyexcel.object.AnnotationMember;
import github.com.icezerocat.component.common.easyexcel.object.builder.AnnotationBuildType;
import github.com.icezerocat.component.common.easyexcel.object.builder.BaseAnnotationBuild;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Description: 注解成员构建工厂
 * CreateDate:  2020/7/24 14:42
 *
 * @author zero
 * @version 1.0
 */
public class FactoryAnnotationBuild {

    /**
     * 添加注解成员变量
     *
     * @param buildField       构建Field
     * @param annotationMember 注解对象数据
     */
    public static void addMemberValue(BaseAnnotationBuild buildField, AnnotationMember annotationMember) {
        String type = annotationMember.getType().toLowerCase();
        String[] strings = type.split("_");
        type = strings[0].toUpperCase();
        if (strings.length > 1 && "arr".equals(strings[1])) {
            convertArr(type, buildField, annotationMember.getMember(), annotationMember.getValueList());
        } else {
            convert(type, buildField, annotationMember.getMember(), annotationMember.getValue());
        }
    }

    /**
     * 添加普通类型注解成员
     *
     * @param type       数据类型
     * @param buildField 构建Field
     * @param member     成员名字
     * @param value      值
     */
    private static void convert(String type, BaseAnnotationBuild buildField, String member, Object value) {
        String s = String.valueOf(value);
        switch (AnnotationBuildType.valueOf(type)) {
            case STRING:
                buildField.addMemberValue(member, s);
                break;
            case INTEGER:
                Integer integer = Optional.ofNullable(s).map(Ints::tryParse).orElse(0);
                buildField.addMemberValue(member, integer);
                break;
            case BYTE:
                Byte aByte = Byte.valueOf(s);
                if (aByte != null) {
                    buildField.addMemberByteValue(member, aByte);
                }
                break;
            case CHAR:
                char cv = 0;
                for (char c : s.toCharArray()) {
                    cv += c;
                }
                buildField.addMemberValue(member, cv);
                break;
            case CLASS:
                buildField.addMemberValueByClassName(member, s);
                break;
            case SHORT:
                buildField.addMemberValue(member, Short.parseShort(s));
                break;
            case DOUBLE:
                buildField.addMemberValue(member, Double.valueOf(s));
                break;
            case BOOLEAN:
                buildField.addMemberValue(member, Boolean.parseBoolean(s));
                break;
            default:
                buildField.addMemberValue(member, s);
        }
    }

    /**
     * 添加数组类型注解成员
     *
     * @param type       数据类型
     * @param buildField 构建Field
     * @param member     成员名字
     * @param valueList  数组值
     */
    private static void convertArr(String type, BaseAnnotationBuild buildField, String member, List valueList) {
        switch (AnnotationBuildType.valueOf(type)) {
            case INTEGER:
                List<Integer> integerList = new ArrayList<>();
                for (Object o : valueList) {
                    Integer integer = Optional.ofNullable(String.valueOf(o)).map(Ints::tryParse).orElse(0);
                    integerList.add(integer);
                }
                buildField.addMemberValueArr(member, integerList.toArray(new Integer[0]));
                break;
            case BYTE:
                List<Byte> oList = new ArrayList<>();
                for (Object o : valueList) {
                    oList.add(Byte.valueOf(String.valueOf(o)));
                }
                buildField.addMemberByteValueArr(member, oList.toArray(new Byte[0]));
                break;
            case CLASS:
                List<String> classNameList = new ArrayList<>();
                for (Object o : valueList) {
                    classNameList.add(String.valueOf(o));
                }
                buildField.addMemberValueByClassNameArr(member, classNameList.toArray(new String[0]));
                break;
            case DOUBLE:
                List<Double> doubleList = new ArrayList<>();
                for (Object o : valueList) {
                    doubleList.add(Double.parseDouble(String.valueOf(o)));
                }
                buildField.addMemberValueArr(member, doubleList.toArray(new Double[0]));
                break;
            case BOOLEAN:
                List<Boolean> booleanList = new ArrayList<>();
                for (Object o : valueList) {
                    booleanList.add(Boolean.valueOf(String.valueOf(o)));
                }
                buildField.addMemberValueArr(member, booleanList.toArray(new Boolean[0]));
                break;
            default:
                List<String> stringList = new ArrayList<>();
                for (Object o : valueList) {
                    stringList.add(String.valueOf(o));
                }
                buildField.addMemberValueArr(member, stringList.toArray(new String[0]));
        }
    }
}