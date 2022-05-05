package github.com.icezerocat.component.common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * Created by zmj
 * On 2020/2/10.
 *
 * @author 0.0.0
 */
@Slf4j
public class UploadUtil {

    /**
     * 上传文件
     *
     * @param dataPath      文件路径
     * @param multipartFile 文件
     * @return 保存结果
     */
    public static JSONObject save(String dataPath, MultipartFile multipartFile) {
        JSONObject jsonObject = new JSONObject();
        String msg = "上传文件成功";
        boolean result = true;
        if (Objects.isNull(multipartFile)) {
            msg = "上传文件为空";
            result = false;
        } else {
            if (dataPath.indexOf("/") != 0) {
                dataPath = "/" + dataPath;
            }
            if (!"/".equals(dataPath.substring(dataPath.length() - 1))) {
                dataPath += "/";
            }
            String fileName = multipartFile.getOriginalFilename();
            // 访问url 【url】/uploads/image/【fileName】
            dataPath += fileName;

            String webPath = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/";
            dataPath = webPath + dataPath;
            File file = new File(dataPath);
            if (!file.getParentFile().exists()) {
                boolean mkdirsBl = file.getParentFile().mkdirs();
                if (!mkdirsBl) {
                    msg = "文件已存在或创建文件失败";
                    result = false;
                }
            }

            try {
                multipartFile.transferTo(file);
            } catch (IOException e) {
                msg = "上传文件失败";
                result = false;
                e.printStackTrace();
            }
        }
        jsonObject.put("msg", msg);
        jsonObject.put("result", result);
        if (result) {
            jsonObject.put("dataPath", dataPath);
        } else {
            jsonObject.put("dataPath", "");
        }
        return jsonObject;
    }

    /**
     * 获取项目路径
     *
     * @return 项目路径
     */
    public static String getWebPath() {
        return System.getProperty("user.dir").replaceAll("\\\\", "/") + "/";
    }


    /**
     * 下载resource根目录下的文件
     *
     * @param fileName 文件名
     * @param response 响应
     * @throws IOException io异常
     */
    public static void downloadExcel(String fileName, HttpServletResponse response) throws IOException {
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "max-age=0");
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if (inputStream != null) {
            response.addHeader("Content-Length", String.valueOf(inputStream.available()));
            OutputStream os = response.getOutputStream();
            byte[] bis = new byte[1024];
            while (-1 != inputStream.read(bis)) {
                os.write(bis);
            }
        }
    }
}
