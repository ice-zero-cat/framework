package github.com.icezerocat.core.http;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * HTTP结果封装
 *
 * @author 0.0.0
 */
@SuppressWarnings("unused")
@Data
public class HttpResult<T> implements Serializable {

    final private int code;
    final private String msg;
    final private T data;
    final private long count;

    private HttpResult(Build<T> build) {
        this.code = build.code;
        this.msg = build.msg;
        this.data = build.data;
        this.count = build.count;
    }

    public static class Build<T> {
        private int code = HttpStatus.SC_OK;
        private String msg;
        private T data;
        private long count;

        public static <T> Build<T> getInstance() {
            return new Build<>();
        }

        /**
         * 设置 code
         *
         * @param code code
         * @return build
         */
        public Build<T> setCode(int code) {
            this.code = code;
            return this;
        }

        /**
         * 设置信息
         *
         * @param msg msg
         * @return build
         */
        public Build<T> setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        /**
         * 设置数据
         *
         * @param data data
         * @return build
         */
        public Build<T> setData(T data) {
            this.data = data;
            int count = 0;
            if (data instanceof Collection) {
                count = ((Collection) data).size();
            }
            if (data instanceof Object[]) {
                count = ((Object[]) data).length;
            }
            if (data instanceof Map) {
                count = ((Map) data).size();
            }
            this.setCount(count);
            return this;
        }

        /**
         * 设置总数
         *
         * @param count 总数
         * @return build
         */
        public Build<T> setCount(long count) {
            this.count = count;
            return this;
        }

        /**
         * 完成构建
         *
         * @return httpResult
         */
        public HttpResult<T> complete() {
            return new HttpResult<>(this);
        }
    }

    public static <T> HttpResult<T> error() {
        return HttpResult.Build.<T>getInstance().setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).setMsg("未知异常，请联系管理员").complete();
    }

    public static <T> HttpResult<T> error(String msg) {
        return HttpResult.Build.<T>getInstance().setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).setMsg(msg).complete();
    }

    private static <T> HttpResult<T> error(int code, String msg) {
        return HttpResult.Build.<T>getInstance().setCode(code).setMsg(msg).complete();
    }

    public static <T> HttpResult<T> ok(String msg) {
        return HttpResult.Build.<T>getInstance().setMsg(msg).complete();
    }

    public static <T> HttpResult<T> ok(T data) {
        return HttpResult.Build.<T>getInstance().setData(data).complete();
    }

    public static <T> HttpResult<T> ok() {
        return Build.<T>getInstance().complete();
    }
}
