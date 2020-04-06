package github.com.icezerocat.backup.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;

/**
 * @author 0.0.0
 */
public class FileUtils {


    /**
     * 读取txt文件的内容
     *
     * @param file 想要读取的文件路径
     * @return 返回文件内容
     */
    public static String readFile(String file) {
        return readFile(new File(file));
    }

    /**
     * 读取txt文件的内容
     *
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String readFile(File file) {
        StringBuilder result = new StringBuilder();
        try {
            //构造一个BufferedReader类来读取文件
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                //使用readLine方法，一次读一行
                result.append(System.lineSeparator()).append(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 递归删除文件
     *
     * @param file 文件
     */
    public static void deleteFile(File file) {
        // 判断是否是一个目录, 不是的话跳过, 直接删除; 如果是一个目录, 先将其内容清空.
        if (file.isDirectory()) {
            // 获取子文件/目录
            File[] subFiles = file.listFiles();
            // 遍历该目录
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    // 递归调用删除该文件: 如果这是一个空目录或文件, 一次递归就可删除.
                    // 如果这是一个非空目录, 多次递归清空其内容后再删除
                    deleteFile(subFile);
                }
            }
        }
        // 删除空目录或文件
        boolean delBl = file.delete();
        if (!delBl) {
            System.out.println("删除目录失败");
        }

    }

    /**
     * 获取项目根路径
     *
     * @return 项目根路径
     */
    public static String getProjectPath() {
        String classPath = getClassPath();
        return new File(classPath).getParentFile().getParentFile().getAbsolutePath();
    }

    /**
     * 获取类路径
     *
     * @return 类路径
     */
    public static String getClassPath() {
		return Objects.requireNonNull(FileUtils.class.getClassLoader().getResource("")).getPath();
    }

    public static void main(String[] args) {
//        File file = new File("D:/errlog.txt");
//        System.out.println(readFile(file));

        System.out.println(getClassPath());

        System.out.println(getProjectPath());


    }
}

