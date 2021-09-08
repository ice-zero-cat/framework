package github.com.icezerocat.component.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Description: 媒体对象转file
 * CreateDate:  2021/9/3 13:52
 *
 * @author zero
 * @version 1.0
 */
public class MultipartFileUtil {

    public static File multipartFileToFile(MultipartFile file) throws Exception {

        InputStream ins = file.getInputStream();
        File toFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        inputStreamToFile(ins, toFile);
        ins.close();
        return toFile;
    }

    /**
     * 获取流文件
     *
     * @param ins  输入流
     * @param file 文件
     */
    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
