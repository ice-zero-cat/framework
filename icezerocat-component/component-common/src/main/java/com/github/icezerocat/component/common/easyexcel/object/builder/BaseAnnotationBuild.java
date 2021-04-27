package com.github.icezerocat.component.common.easyexcel.object.builder;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 0.0.0
 * ProjectName: [easyexcel]
 * Package: [com.excel.easyexcel.object.build.BaseAnnotationBuild]
 * Description 类基础注解构建
 * Date 2020/3/13 1:49
 */
@SuppressWarnings("unused")
public abstract class BaseAnnotationBuild {
    public AnnotationsAttribute annotationsAttribute;
    public Annotation annotation;
    public ConstPool constPool;

    /**
     * 添加注解
     *
     * @param annotationClass 注解类
     * @return 类构建
     */
    public BaseAnnotationBuild addAnnotation(Class annotationClass) {
        this.annotation = new Annotation(annotationClass.getTypeName(), this.constPool);
        return this;
    }

    /**
     * 提交类注解构建
     *
     * @return 注解构建基类
     */
    public abstract BaseAnnotationBuild commitAnnotation();

    /**
     * 添加注解的成员和值
     *
     * @param member 注解成员
     * @param value  值
     * @return 构建类
     */
    public BaseAnnotationBuild addMemberValue(String member, @NotNull String value) {
        MemberValue memberValue = new StringMemberValue(value, this.constPool);
        this.annotation.addMemberValue(member, memberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValueArr(String member, @NotNull String[] value) {
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(new StringMemberValue(value[0], this.constPool), this.constPool);
        List<StringMemberValue> stringMemberValueList = new ArrayList<>();
        for (String s : value) {
            stringMemberValueList.add(new StringMemberValue(s, this.constPool));
        }
        arrayMemberValue.setValue(stringMemberValueList.toArray(new StringMemberValue[0]));
        this.annotation.addMemberValue(member, arrayMemberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValue(String member, @NotNull Integer... value) {
        MemberValue memberValue = new IntegerMemberValue(value[0], this.constPool);
        if (value.length > 1) {
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(memberValue, this.constPool);
            List<IntegerMemberValue> integerMemberValueArrayList = new ArrayList<>();
            for (Integer integer : value) {
                integerMemberValueArrayList.add(new IntegerMemberValue(integer, this.constPool));
            }
            arrayMemberValue.setValue(integerMemberValueArrayList.toArray(new IntegerMemberValue[0]));
            memberValue = arrayMemberValue;
        }
        this.annotation.addMemberValue(member, memberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValueArr(String member, @NotNull Integer[] value) {
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(new IntegerMemberValue(value[0], this.constPool), this.constPool);
        List<IntegerMemberValue> integerMemberValueArrayList = new ArrayList<>();
        for (Integer integer : value) {
            integerMemberValueArrayList.add(new IntegerMemberValue(integer, this.constPool));
        }
        arrayMemberValue.setValue(integerMemberValueArrayList.toArray(new IntegerMemberValue[0]));
        this.annotation.addMemberValue(member, arrayMemberValue);
        return this;
    }


    public BaseAnnotationBuild addMemberValue(String member, @NotNull Boolean value) {
        MemberValue memberValue = new BooleanMemberValue(value, this.constPool);
        this.annotation.addMemberValue(member, memberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValueArr(String member, @NotNull Boolean[] value) {
        MemberValue memberValue = new BooleanMemberValue(value[0], this.constPool);
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(memberValue, this.constPool);
        List<BooleanMemberValue> booleanMemberValues = new ArrayList<>();
        for (Boolean b : value) {
            booleanMemberValues.add(new BooleanMemberValue(b, this.constPool));
        }
        arrayMemberValue.setValue(booleanMemberValues.toArray(new BooleanMemberValue[0]));
        this.annotation.addMemberValue(member, memberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValue(String member, @NotNull short... value) {
        MemberValue memberValue = new ShortMemberValue(value[0], this.constPool);
        if (value.length > 1) {
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(memberValue, this.constPool);
            List<ShortMemberValue> shortMemberValues = new ArrayList<>();
            for (short s : value) {
                shortMemberValues.add(new ShortMemberValue(s, this.constPool));
            }
            arrayMemberValue.setValue(shortMemberValues.toArray(new ShortMemberValue[0]));
        }
        this.annotation.addMemberValue(member, memberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValueArr(String member, @NotNull short[] value) {
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(new ShortMemberValue(value[0], this.constPool), this.constPool);
        List<ShortMemberValue> shortMemberValues = new ArrayList<>();
        for (short s : value) {
            shortMemberValues.add(new ShortMemberValue(s, this.constPool));
        }
        arrayMemberValue.setValue(shortMemberValues.toArray(new ShortMemberValue[0]));
        this.annotation.addMemberValue(member, arrayMemberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValue(String member, @NotNull Annotation... value) {
        MemberValue memberValue = new AnnotationMemberValue(value[0], this.constPool);
        if (value.length > 1) {
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(memberValue, this.constPool);
            List<AnnotationMemberValue> annotationMemberValues = new ArrayList<>();
            for (Annotation annotation : value) {
                annotationMemberValues.add(new AnnotationMemberValue(annotation, this.constPool));
            }
            arrayMemberValue.setValue(annotationMemberValues.toArray(new AnnotationMemberValue[0]));
        }
        this.annotation.addMemberValue(member, memberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValueArr(String member, @NotNull Annotation[] value) {
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(new AnnotationMemberValue(value[0], this.constPool), this.constPool);
        List<AnnotationMemberValue> annotationMemberValues = new ArrayList<>();
        for (Annotation annotation : value) {
            annotationMemberValues.add(new AnnotationMemberValue(annotation, this.constPool));
        }
        arrayMemberValue.setValue(annotationMemberValues.toArray(new AnnotationMemberValue[0]));
        this.annotation.addMemberValue(member, arrayMemberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberByteValue(String member, @NotNull Byte... value) {
        MemberValue memberValue = new ByteMemberValue(value[0], this.constPool);
        if (value.length > 1) {
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(memberValue, this.constPool);
            List<ByteMemberValue> byteMemberValues = new ArrayList<>();
            for (Byte b : value) {
                byteMemberValues.add(new ByteMemberValue(b, this.constPool));
            }
            arrayMemberValue.setValue(byteMemberValues.toArray(new ByteMemberValue[0]));
        }
        this.annotation.addMemberValue(member, memberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberByteValueArr(String member, @NotNull Byte[] value) {
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(new ByteMemberValue(value[0], this.constPool), this.constPool);
        List<ByteMemberValue> byteMemberValues = new ArrayList<>();
        for (Byte b : value) {
            byteMemberValues.add(new ByteMemberValue(b, this.constPool));
        }
        arrayMemberValue.setValue(byteMemberValues.toArray(new ByteMemberValue[0]));
        this.annotation.addMemberValue(member, arrayMemberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValue(String member, @NotNull char... value) {
        MemberValue memberValue = new CharMemberValue(value[0], this.constPool);
        if (value.length > 1) {
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(memberValue, this.constPool);
            List<CharMemberValue> byteMemberValues = new ArrayList<>();
            for (char o : value) {
                byteMemberValues.add(new CharMemberValue(o, this.constPool));
            }
            arrayMemberValue.setValue(byteMemberValues.toArray(new CharMemberValue[0]));
        }
        this.annotation.addMemberValue(member, memberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValueArr(String member, @NotNull char[] value) {
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(new CharMemberValue(value[0], this.constPool), this.constPool);
        List<CharMemberValue> byteMemberValues = new ArrayList<>();
        for (char o : value) {
            byteMemberValues.add(new CharMemberValue(o, this.constPool));
        }
        arrayMemberValue.setValue(byteMemberValues.toArray(new CharMemberValue[0]));
        this.annotation.addMemberValue(member, arrayMemberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValue(String member, @NotNull Double... value) {
        MemberValue memberValue = new DoubleMemberValue(value[0], this.constPool);
        if (value.length > 1) {
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(memberValue, this.constPool);
            List<DoubleMemberValue> byteMemberValues = new ArrayList<>();
            for (Double o : value) {
                byteMemberValues.add(new DoubleMemberValue(o, this.constPool));
            }
            arrayMemberValue.setValue(byteMemberValues.toArray(new DoubleMemberValue[0]));
        }
        this.annotation.addMemberValue(member, memberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValueArr(String member, @NotNull Double[] value) {
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(new DoubleMemberValue(value[0], this.constPool), this.constPool);
        List<DoubleMemberValue> byteMemberValues = new ArrayList<>();
        for (Double o : value) {
            byteMemberValues.add(new DoubleMemberValue(o, this.constPool));
        }
        arrayMemberValue.setValue(byteMemberValues.toArray(new DoubleMemberValue[0]));
        this.annotation.addMemberValue(member, arrayMemberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValueByClassName(String member, @NotNull String... value) {
        MemberValue memberValue = new ClassMemberValue(value[0], this.constPool);
        if (value.length > 1) {
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(memberValue, this.constPool);
            List<ClassMemberValue> byteMemberValues = new ArrayList<>();
            for (String o : value) {
                byteMemberValues.add(new ClassMemberValue(o, this.constPool));
            }
            arrayMemberValue.setValue(byteMemberValues.toArray(new ClassMemberValue[0]));
        }
        this.annotation.addMemberValue(member, memberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValueByClassNameArr(String member, @NotNull String[] value) {
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(new ClassMemberValue(value[0], this.constPool), this.constPool);
        List<ClassMemberValue> byteMemberValues = new ArrayList<>();
        for (String o : value) {
            byteMemberValues.add(new ClassMemberValue(o, this.constPool));
        }
        arrayMemberValue.setValue(byteMemberValues.toArray(new ClassMemberValue[0]));
        this.annotation.addMemberValue(member, arrayMemberValue);
        return this;
    }

    public BaseAnnotationBuild addMemberValue(String member, @NotNull MemberValue memberValue) {
        this.annotation.addMemberValue(member, memberValue);
        return this;
    }
}
