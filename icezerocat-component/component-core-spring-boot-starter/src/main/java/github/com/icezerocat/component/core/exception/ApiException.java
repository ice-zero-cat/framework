package github.com.icezerocat.component.core.exception;

import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * ProjectName: [icezero-system]
 * Package:     [com.githup.icezerocat.core.exception.ZeroException]
 * Description: 自定义异常
 * CreateDate:  2020/4/20 22:58
 *
 * @author 0.0.0
 * @version 1.0
 */
@ToString
@SuppressWarnings("unused")
public class ApiException extends RuntimeException {
    private static final long serialVersionUID = -7230273470995591933L;

    private String message;
    private int code = HttpStatus.INTERNAL_SERVER_ERROR.value();


    public ApiException(String message) {
        super(message);
        this.message = message;
    }

    public ApiException(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public ApiException(String message, Throwable cause, int code) {
        super(message, cause);
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
