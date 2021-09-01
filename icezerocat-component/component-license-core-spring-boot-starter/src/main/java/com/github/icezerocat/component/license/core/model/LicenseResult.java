package com.github.icezerocat.component.license.core.model;


import de.schlichtherle.license.LicenseContent;

/**
 * Description: License证书验证结果对象
 * CreateDate:  2021/8/30 19:58
 *
 * @author zero
 * @version 1.0
 */
public class LicenseResult {

    /** 检验结果 */
    private Boolean result;
    /** 附加信息 */
    private String message;
    /** 证书内容 */
    private LicenseContent content;
    /** 检验失败错误 */
    private Exception exception;

    public LicenseResult(LicenseContent content) {
        this.result = true;
        this.content = content;
    }

    public LicenseResult(String message, LicenseContent content) {
        this.result = true;
        this.message = message;
        this.content = content;
    }

    public LicenseResult(Exception exception) {
        this.result = false;
        this.exception = exception;
    }

    public LicenseResult(String message, Exception exception) {
        this.result = false;
        this.message = message;
        this.exception = exception;
    }

    public LicenseResult(boolean result , String message, Exception exception) {
        this.result = result;
        this.message = message;
        this.exception = exception;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LicenseContent getContent() {
        return content;
    }

    public void setContent(LicenseContent content) {
        this.content = content;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
