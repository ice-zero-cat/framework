package github.com.icezerocat.component.common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by zmj
 * On 2020/2/10.
 *
 * @author 0.0.0
 */
@Slf4j
public class UploadUtil {

    public static JSONObject save(String dataPath, MultipartFile multipartFile) {
        JSONObject jsonObject = new JSONObject();
        String msg = "上传文件成功";
        boolean result = true;
        if (Objects.isNull(multipartFile)) {
            msg = "上传文件为空";
            result = false;
        } else {
            if (dataPath.indexOf("/") != 0) {
                dataPath =  "/" + dataPath;
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
}
