package com.github.icezerocat.component.license.core.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 日志输出辅助类
 * CreateDate:  2021/8/30 19:58
 *
 * @author zero
 * @version 1.0
 */
public class LoggerHelper {
    private static Logger logger = LoggerFactory.getLogger(LoggerHelper.class);
    public LoggerHelper() {
    }

    public static void info(String message) {
        logger.info(message);
    }
    public static void debug(String message) {
        logger.debug(message);
    }
    public static void error(String message, Exception ex) {
        logger.error(message, ex);
    }
    public static void error(Integer errCode, String message) {
        logger.error("错误码：" + errCode + "，错误消息：" + message);
    }
    public static void error(String message) {
        logger.error("错误消息：" + message);
    }
    public static void error(Integer errCode, String message, Exception ex) {
        logger.error("错误码：" + errCode + "，错误消息：" + message + ",异常信息：" + ex.getMessage());
    }
}
