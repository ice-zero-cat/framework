package github.com.icezerocat.core.http;

import java.io.Serializable;

/**
 * HTTP结果封装
 *
 * @author 0.0.0
 */
@SuppressWarnings("all")
public class HttpResult implements Serializable {

    final private int code;
    final private String msg;
    final private Object data;

    private HttpResult(Build build) {
        this.code = build.code;
        this.msg = build.msg;
        this.data = build.data;
    }

    public static class Build {
        private int code = HttpStatus.SC_OK;
        private String msg;
        private Object data;

        public static Build getInstance() {
            return new Build();
        }

        /**
         * 设置 code
         *
         * @param code code
         * @return build
         */
        public Build setCode(int code) {
            this.code = code;
            return this;
        }

        /**
         * 设置信息
         *
         * @param msg msg
         * @return build
         */
        public Build setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        /**
         * 设置数据
         *
         * @param data data
         * @return build
         */
        public Build setData(Object data) {
            this.data = data;
            return this;
        }

        /**
         * 完成构建
         *
         * @return httpResult
         */
        public HttpResult complete() {
            return new HttpResult(this);
        }
    }

    public static HttpResult error() {
        return HttpResult.Build.getInstance().setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).setMsg("未知异常，请联系管理员").complete();
    }

    public static HttpResult error(String msg) {
        return HttpResult.Build.getInstance().setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).setMsg(msg).complete();
    }

    private static HttpResult error(int code, String msg) {
        return HttpResult.Build.getInstance().setCode(code).setMsg(msg).complete();
    }

    public static HttpResult ok(String msg) {
        return HttpResult.Build.getInstance().setMsg(msg).complete();
    }

    public static HttpResult ok(Object data) {
        return HttpResult.Build.getInstance().setData(data).complete();
    }

    public static HttpResult ok() {
        return HttpResult.Build.getInstance().complete();
    }

}
