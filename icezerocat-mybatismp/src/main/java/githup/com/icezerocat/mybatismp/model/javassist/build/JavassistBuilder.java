package githup.com.icezerocat.mybatismp.model.javassist.build;


import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

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
    public static final String DIRECTORY_NAME = "./target/classes";
    public static final String PACKAGE_NAME = "com.githup.icezerocat.ap.object";
    private CtClass ctClass;

    /**
     * 创建实例
     *
     * @return 对象构建实例
     */
    public static JavassistBuilder newInstance() {
        return new JavassistBuilder();
    }

    /**
     * 创建构建类build
     *
     * @param className 类名
     * @return 构建类
     */
    public JavassistBuilder.BuildClass newBuildClass(String className) {
        return newBuildClass(JavassistBuilder.PACKAGE_NAME, className);
    }

    /**
     * 创建构建类build
     *
     * @param packageName 包名
     * @param className   类名
     * @return 构建类
     */
    public JavassistBuilder.BuildClass newBuildClass(String packageName, String className) {
        JavassistBuilder.BuildClass buildClass = new JavassistBuilder.BuildClass(packageName, className);
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
        private String classPath;

        /**
         * 创建类
         *
         * @param packageName 包名（包路径）
         * @param className   类名
         */
        BuildClass(String packageName, String className) {
            this.classPath = packageName + "." + upperCase(className);
            //解冻
            try {
                CtClass ctClass = ClassPool.getDefault().get(classPath);
                if (ctClass.isFrozen()) {
                    ctClass.defrost();
                }
            } catch (NotFoundException ignored) {
            }
        }

        /**
         * 创建类
         *
         * @return BuildClass
         */
        public BuildClass markClass() {
            this.ctClass = ClassPool.getDefault().makeClass(this.classPath);
            //创建构造函数
            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, this.ctClass);
            try {
                ctConstructor.setBody("{}");
                this.ctClass.addConstructor(ctConstructor);
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
            return this;
        }

        /**
         * 创建接口
         *
         * @return BuildClass
         */
        public BuildClass makeInterface() {
            this.ctClass = ClassPool.getDefault().makeInterface(this.classPath);
            return this;
        }

        /**
         * 创建接口
         *
         * @param superclass 父类
         * @return BuildClass
         */
        public BuildClass setSuperclass(Class superclass) {
            ClassPool classPool = ClassPool.getDefault();
            CtClass superCtClass = classPool.makeInterface(superclass.getName());
            this.ctClass = classPool.makeInterface(this.classPath, superCtClass);
            return this;
        }

        /**
         * 设置类实现的接口
         *
         * @param interFacesClass 需要实现的接口类
         * @return 类构建
         */
        public BuildClass setInterfaces(Class interFacesClass) {
            CtClass[] interfaces = new CtClass[]{ClassPool.getDefault().makeClass(interFacesClass.getName())};
            this.ctClass.setInterfaces(interfaces);
            return this;
        }


        /**
         * 构建注解
         *
         * @param c 注解类
         * @return 类构建
         */
        public BuildClass buildAnnotations(Class c) {
            this.classFile = this.ctClass.getClassFile();
            this.constPool = this.classFile.getConstPool();
            this.annotationsAttribute = new AnnotationsAttribute(this.constPool, AnnotationsAttribute.visibleTag);
            this.addAnnotation(c);
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


        public Class getGenerateClass() {
            try {
                return this.ctClass.toClass();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 输出class文件
         */
        public void writeFile() {
            try {
                //输出class文件
                this.ctClass.writeFile(JavassistBuilder.DIRECTORY_NAME);
            } catch (IOException | CannotCompileException e) {
                e.printStackTrace();
            }
        }

        /**
         * 提交类构建
         */
        public void commit() {
            this.writeFile();
            this.ctClass.detach();
        }

        /**
         * 获取类路径名
         *
         * @return 类路径名
         */
        public String getName() {
            try {
                return ResourceUtils.getURL("classpath:").getPath() + ctClass.getName().replaceAll("\\.", "/").substring(1);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
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
