package github.com.icezerocat.component.common.utils;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.Stack;

/**
 * Description: 类工具类
 * CreateDate:  2020/11/19 13:04
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
public class ClassUtils {
    /**
     * 加载指定路径下所有Class文件
     *
     * @param classPath class文件所在根路径
     * @return class
     * @throws NoSuchMethodException  找不到方法类型
     * @throws MalformedURLException  出现了错误的url，字符串不规范、不符合协议、解析错误
     * @throws ClassNotFoundException 找不到类
     */
    public static Set<Class<?>> searchClassInPath(String classPath) throws NoSuchMethodException, MalformedURLException, ClassNotFoundException {
        Set<Class<?>> classSet = Sets.newHashSet();
        // 设置class文件所在根路径
        // 例如/usr/java/classes下有一个test.App类，则/usr/java/classes即这个类的根路径，
        // 而.class文件的实际位置是/usr/java/classes/test/App.class
        File clazzPath = new File(classPath);
        // 记录加载.class文件的数量
        int clazzCount = 0;
        if (clazzPath.exists() && clazzPath.isDirectory()) {
            // 获取路径长度
            int clazzPathLen = clazzPath.getAbsolutePath().length() + 1;

            Stack<File> stack = new Stack<>();
            stack.push(clazzPath);

            // 遍历类路径
            while (!stack.isEmpty()) {
                File path = stack.pop();
                File[] classFiles = path.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith(".class"));
                assert classFiles != null;
                for (File subFile : classFiles) {
                    if (subFile.isDirectory()) {
                        stack.push(subFile);
                    } else {
                        if (clazzCount++ == 0) {
                            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                            boolean accessible = method.isAccessible();
                            try {
                                if (!accessible) {
                                    method.setAccessible(true);
                                }
                                // 设置类加载器
                                URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                                // 将当前类路径加入到类加载器中
                                method.invoke(classLoader, clazzPath.toURI().toURL());
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            } finally {
                                method.setAccessible(accessible);
                            }
                        }
                        // 文件名称
                        String className = subFile.getAbsolutePath();
                        className = className.substring(clazzPathLen, className.length() - 6);
                        className = className.replace(File.separatorChar, '.');
                        // 加载Class类
                        Class<?> aClass = Class.forName(className);
                        classSet.add(aClass);
                        log.info("读取应用程序类文件[class={}]", className);
                    }
                }
            }
        }
        return classSet;
    }

    /**
     * 加载指定类路径下指定文件名
     *
     * @param classPath     class文件所在的路径
     * @param fullClassName class名字
     * @return class
     * @throws NoSuchMethodException  找不到方法类型
     * @throws MalformedURLException  出现了错误的url，字符串不规范、不符合协议、解析错误
     * @throws ClassNotFoundException 找不到类
     */
    public static Class<?> searchClassByClassName(String classPath, String fullClassName) throws NoSuchMethodException, MalformedURLException, ClassNotFoundException {
        return loadClass(classPath, fullClassName, ClassLoader.getSystemClassLoader());
    }

    /**
     * 加载指定类路径下指定文件名
     *
     * @param classPath     class文件所在的路径
     * @param fullClassName class名字
     * @param classLoader   类加载器
     * @return class
     * @throws NoSuchMethodException  找不到方法类型
     * @throws MalformedURLException  出现了错误的url，字符串不规范、不符合协议、解析错误
     * @throws ClassNotFoundException 找不到类
     */
    public static Class<?> searchClassByClassName(String classPath, String fullClassName, ClassLoader classLoader) throws NoSuchMethodException, MalformedURLException, ClassNotFoundException {
        return loadClass(classPath, fullClassName, classLoader);
    }

    /**
     * 加载指定类路径下指定文件名
     *
     * @param classPath     class文件所在的路径
     * @param fullClassName class名字
     * @param classLoader   类加载器
     * @return class
     * @throws NoSuchMethodException  找不到方法类型
     * @throws MalformedURLException  出现了错误的url，字符串不规范、不符合协议、解析错误
     * @throws ClassNotFoundException 找不到类
     */
    private static Class<?> loadClass(String classPath, String fullClassName, ClassLoader classLoader) throws NoSuchMethodException, MalformedURLException, ClassNotFoundException {
        Class<?> aClass = null;
        // 设置class文件所在根路径
        // 例如/usr/java/classes下有一个test.App类，则/usr/java/classes即这个类的根路径，
        // 而.class文件的实际位置是/usr/java/classes/test/App.class
        File clazzPath = new File(classPath);

        // 记录加载.class文件的数量
        int clazzCount = 0;

        if (clazzPath.exists() && clazzPath.isDirectory()) {
            // 获取路径长度
            int clazzPathLen = clazzPath.getAbsolutePath().length() + 1;

            Stack<File> stack = new Stack<>();
            stack.push(clazzPath);

            // 遍历类路径
            while (!stack.isEmpty()) {
                File path = stack.pop();
                File[] classFiles = path.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith(".class"));
                assert classFiles != null;
                for (File subFile : classFiles) {
                    if (subFile.isDirectory()) {
                        stack.push(subFile);
                    } else {
                        if (clazzCount++ == 0) {
                            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                            boolean accessible = method.isAccessible();
                            try {
                                if (!accessible) {
                                    method.setAccessible(true);
                                }
                                // 设置类加载器
                                URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
                                // 将当前类路径加入到类加载器中
                                method.invoke(urlClassLoader, clazzPath.toURI().toURL());
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            } finally {
                                method.setAccessible(accessible);
                            }
                        }
                        // 文件名称
                        String className = subFile.getAbsolutePath();
                        className = className.substring(clazzPathLen, className.length() - 6);
                        className = className.replace(File.separatorChar, '.');
                        // 加载Class类
                        if (className.equals(fullClassName)) {
                            aClass = Class.forName(className, true, classLoader);
                            //TODO 待开发热部署
                            //aClass = MpClassLoader.findClassLoader(classPath, className);
                            log.info("ClassUtils-loading……[class({})={}]", aClass.hashCode(), className);
                            break;
                        }
                    }
                }
            }
        }
        return aClass;
    }
}
