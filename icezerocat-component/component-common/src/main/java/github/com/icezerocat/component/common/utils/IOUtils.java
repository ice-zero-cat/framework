package github.com.icezerocat.component.common.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * IO相关工具类
 *
 * @author 0.0.0
 */
@SuppressWarnings("unused")
public class IOUtils {

    /**
     * 关闭对象，连接
     *
     * @param closeable 流
     */
    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }

    /**
     * 获取Excel输出流
     *
     * @param response 流
     * @param fileName 文件名
     * @return 输出流
     * @throws IOException IO异常
     */
    public static OutputStream getExcelOutputStream(HttpServletResponse response, String fileName) throws IOException {
        response.setCharacterEncoding("utf-8");
        fileName = fileName + ".xlsx";
        //初始化返回
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/msexcel");
        response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response.getOutputStream();
    }
}
