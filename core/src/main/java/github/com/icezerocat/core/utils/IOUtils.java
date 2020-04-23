package github.com.icezerocat.core.utils;

import java.io.Closeable;
import java.io.IOException;

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
}
