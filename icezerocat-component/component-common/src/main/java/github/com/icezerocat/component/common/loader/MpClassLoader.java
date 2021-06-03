package github.com.icezerocat.component.common.loader;

import lombok.SneakyThrows;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Description: mu类加载器
 * CreateDate:  2021/6/2 23:12
 *
 * @author zero
 * @version 1.0
 */
public class MpClassLoader extends URLClassLoader {

    public MpClassLoader(URL[] urls) {
        super(urls);
    }

    private static String CLASS_PATH = "";

    /**
     * 构造实例
     *
     * @param classPath 类路径（C:\javaComponent\icezero-study\apTarget\classes）
     * @return MpClassLoader
     */
    @SneakyThrows
    public static MpClassLoader instance(String classPath) {
        CLASS_PATH = classPath;
        URI uri = new File(classPath).toURI();
        URL[] urls = {uri.toURL()};
        return new MpClassLoader(urls);
    }

    /**
     * 构造实例
     *
     * @param classPath     类路径（C:\javaComponent\icezero-study\apTarget\classes）
     * @param fullClassName 包名（github.com.icezerocat.ap.TKnowledgeBase）
     * @return aClass
     */
    @SneakyThrows
    public static Class<?> findClassLoader(String classPath, String fullClassName) {
        CLASS_PATH = classPath;
        URI uri = new File(classPath).toURI();
        URL[] urls = {uri.toURL()};
        return new MpClassLoader(urls).findClass(fullClassName);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData;
        //1、读取类文件的字节码
        try (InputStream ins = new FileInputStream(classNameToPath(name)); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int bytesNumRead;
            while ((bytesNumRead = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesNumRead);
            }
            classData = baos.toByteArray();
        } catch (IOException e) {
            throw new ClassNotFoundException();
        }
        //2、生成class对象
        if (classData == null) {
            throw new ClassNotFoundException();
        } else {
            return defineClass(name, classData, 0, classData.length);
        }
    }

    /**
     * 类名转路径
     *
     * @param className 类名
     * @return 真实文件路径
     */
    private String classNameToPath(String className) {
        return CLASS_PATH + File.separatorChar
                + className.replace('.', File.separatorChar) + ".class";
    }

}
