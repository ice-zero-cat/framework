package github.com.icezerocat.component.db.builder;


import github.com.icezerocat.component.common.easyexcel.object.builder.BaseAnnotationBuild;
import github.com.icezerocat.component.common.utils.ClassUtils;
import github.com.icezerocat.component.core.config.ProjectPathConfig;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 构建类class（分析、编辑和创建Java字节码）
 * <p>
 * Created by zmj
 * On 2019/12/25.
 *
 * @author 0.0.0
 */
@SuppressWarnings("unused")
@Slf4j
public class JavassistBuilder {
    /**
     * 输出class目录
     */
    public static final String PACKAGE_NAME = "github.com.icezerocat.ap.";
    public static final String DIRECTORY_NAME = "apTarget/classes";
    public static final Map<String, Class> PACKAGE_NAME_TO_CLASS_MAP = new HashMap<>();
    private CtClass ctClass;
    /*private static StringBuilder fieldBuilder = new StringBuilder();*/

    /**
     * 创建构建类build
     *
     * @param className 类名
     * @return 构建类
     */
    public JavassistBuilder.BuildClass newBuildClass(String className) {
        JavassistBuilder.BuildClass buildClass = new JavassistBuilder.BuildClass(className);
        this.ctClass = buildClass.getCtClass();
        return buildClass;
    }

    /**
     * 创建类方法build
     *
     * @return 构建类
     */
    public JavassistBuilder.BuildMethod newBuildMethod() {
        return new JavassistBuilder.BuildMethod(this.ctClass);
    }

    /**
     * 创建构建类属性build
     *
     * @return 构建类
     */
    public JavassistBuilder.BuildField newBuildField() {
        return new JavassistBuilder.BuildField(this.ctClass);
    }

    /**
     * 类构建
     */
    public static class BuildClass extends BaseAnnotationBuild {
        private CtClass ctClass;
        private ClassFile classFile;

        /**
         * 创建类
         *
         * @param className 类名
         */
        BuildClass(String className) {
            String classPath = JavassistBuilder.PACKAGE_NAME + upperCase(className);

            //解冻
            try {
                CtClass ctClass = ClassPool.getDefault().get(classPath);
                if (ctClass.isFrozen()) {
                    ctClass.defrost();
                }
            } catch (NotFoundException ignored) {
            }

            this.ctClass = ClassPool.getDefault().makeClass(classPath);
            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, this.ctClass);
            try {
                ctConstructor.setBody("{}");
                this.ctClass.addConstructor(ctConstructor);
                this.classFile = this.ctClass.getClassFile();
                this.constPool = this.classFile.getConstPool();
                this.annotationsAttribute = new AnnotationsAttribute(this.constPool, AnnotationsAttribute.visibleTag);
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }

        /**
         * 设置类实现的接口
         *
         * @param interFacesClass 需要实现的接口类
         * @return 类构建对象
         */
        public BuildClass setInterfaces(Class interFacesClass) {
            CtClass[] interfaces = new CtClass[]{ClassPool.getDefault().makeClass(interFacesClass.getName())};
            this.ctClass.setInterfaces(interfaces);
            return this;
        }

        /**
         * 提交类注解构建
         */
        @Override
        public BuildClass commitAnnotation() {
            this.annotationsAttribute.addAnnotation(this.annotation);
            this.classFile.addAttribute(this.annotationsAttribute);
            return this;
        }

        /**
         * 获取ctClass
         *
         * @return ctClass
         */
        public CtClass getCtClass() {
            return this.ctClass;
        }

        /**
         * 通过类锁定当前项目字节码路径
         *
         * @return Class
         */
        public Class writeFile() {
            String classSysPath = ProjectPathConfig.PROJECT_PATH + JavassistBuilder.DIRECTORY_NAME;
            log.debug("writeFileByClass:{}", classSysPath);
            try {
                //添加toString方法。
                /*String body = "{\n\t\t" + fieldBuilder.toString() + ";\n\t\t" + "return sb.toString();" + "\n}";
                BuildMethod buildMethod = new BuildMethod(this.ctClass);
                buildMethod.addMethod(ClassPool.getDefault().get(String.class.getName()), "toString", new CtClass[]{}, body).addAnnotation(Override.class).commitAnnotation();*/

                //输出class文件
                this.ctClass.writeFile(classSysPath);
                Class tClass = ClassUtils.searchClassByClassName(
                        classSysPath,
                        this.ctClass.getName(), Thread.currentThread().getContextClassLoader().getParent());
                PACKAGE_NAME_TO_CLASS_MAP.put(tClass.getName(), tClass);
                return tClass;
            } catch (IOException | CannotCompileException | ClassNotFoundException | NoSuchMethodException e) {
                log.error("Javassist生成class出错：{}", e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 方法构建
     */
    public static class BuildMethod extends BaseAnnotationBuild {
        private int modifiers = Modifier.PUBLIC;
        private CtClass ctClass;
        private MethodInfo methodInfo;

        BuildMethod(CtClass ctClass) {
            this.ctClass = ctClass;
            this.constPool = ctClass.getClassFile().getConstPool();
        }

        /**
         * 添加方法
         *
         * @param returnType 方法类型
         * @param methodName 方法名
         * @param parameters 参数
         * @param bodySrc    方法体
         * @return 构建类
         */
        public BuildMethod addMethod(CtClass returnType, String methodName, CtClass[] parameters, String bodySrc) {
            CtMethod ctMethod = new CtMethod(returnType, methodName, parameters, this.ctClass);
            this.methodInfo = ctMethod.getMethodInfo();
            this.annotationsAttribute = new AnnotationsAttribute(this.constPool, AnnotationsAttribute.visibleTag);
            ctMethod.setModifiers(this.modifiers);
            this.modifiers = Modifier.PUBLIC;
            try {
                ctMethod.setBody(bodySrc);
                this.ctClass.addMethod(ctMethod);
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
            return this;
        }

        /**
         * 设置修饰符（public、private的代号-Modifier.xxx）
         *
         * @param modifiers 修饰符代号
         * @return 构建类
         */
        public BuildMethod setModifiers(int modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        @Override
        public BuildMethod commitAnnotation() {
            this.annotationsAttribute.addAnnotation(this.annotation);
            this.methodInfo.addAttribute(this.annotationsAttribute);
            return this;
        }
    }

    /**
     * 属性字段构建
     */
    public static class BuildField extends BaseAnnotationBuild {
        private ClassPool classPool;
        private FieldInfo fieldInfo;
        private CtClass ctClass;
        private int modifiers = Modifier.PRIVATE;

        BuildField(CtClass ctClass) {
            this.classPool = ClassPool.getDefault();
            this.ctClass = ctClass;
            this.constPool = ctClass.getClassFile().getConstPool();

            //初始化字段
            /*fieldBuilder = new StringBuilder();
            fieldBuilder.append("StringBuffer sb = new StringBuffer(); \n\t\t")
                    .append("sb.append(\"\\n\")\n\t\t")
                    .append(".append(\"[")
                    .append(ctClass.getName())
                    .append("]\")\n\t\t")
                    .append(".append(\"\\n\\t\")");*/

        }

        /**
         * 添加字段(默认私有)
         *
         * @param fieldType 字段类型
         * @param fieldName 字段名
         * @return 构造器
         */
        public BuildField addField(String fieldType, String fieldName) {
            try {
                CtField ctField = new CtField(this.classPool.get(fieldType), fieldName, this.ctClass);
                ctField.setModifiers(this.modifiers);
                this.fieldInfo = ctField.getFieldInfo();
                this.ctClass.addField(ctField);
                this.annotationsAttribute = new AnnotationsAttribute(this.constPool, AnnotationsAttribute.visibleTag);
                //类添加get、set方法
                this.ctClass.addMethod(CtNewMethod.setter("set" + upperCase(fieldName), ctField));
                this.ctClass.addMethod(CtNewMethod.getter("get" + upperCase(fieldName), ctField));
                //初始化
                this.modifiers = Modifier.PRIVATE;
                //toString字段
                /*fieldBuilder.append("\n\t\t.append(")
                        .append("\"")
                        .append(fieldName)
                        .append(":")
                        .append("\")\n\t\t")
                        .append(".append(this.")
                        .append(fieldName)
                        .append(")\n\t\t")
                        .append(".append(\"\\n\\t\")");*/

            } catch (CannotCompileException | NotFoundException e) {
                e.printStackTrace();
            }
            return this;
        }

        /**
         * 设置修饰符（public、private的代号-Modifier.xxx）
         *
         * @param modifiers 修饰符代号
         * @return 构建类
         */
        public BuildField setModifiers(int modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        @Override
        public BuildField commitAnnotation() {
            this.annotationsAttribute.addAnnotation(this.annotation);
            this.fieldInfo.addAttribute(this.annotationsAttribute);
            return this;
        }
    }


    /**
     * 字符串首字母大写
     *
     * @param str 字符串
     * @return 首字母大写字符串
     */
    private static String upperCase(String str) {
        char[] ch = str.toCharArray();
        char start = 'a';
        char end = 'z';
        if (ch[0] >= start && ch[0] <= end) {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}
