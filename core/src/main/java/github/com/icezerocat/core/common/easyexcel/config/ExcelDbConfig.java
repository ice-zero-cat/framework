package github.com.icezerocat.core.common.easyexcel.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Description: db配置
 * CreateDate:  2020/7/15 10:24
 *
 * @author zero
 * @version 1.0
 */
@Data
@Component
@PropertySource(value = {"classpath:application.properties", "classpath:application.yml"}, ignoreResourceNotFound = true)
public class ExcelDbConfig {
    /**
     * 数据库连接地址
     */
    @Value("${jdbc.url}")
    private String url;
    /**
     * 数据库用户名
     */
    @Value("${jdbc.username}")
    private String username;
    /**
     * 数据库用户密码
     */
    @Value("${jdbc.password}")
    private String password;
    /**
     * 数据库驱动类名
     */
    @Value("${jdbc.driverClassName}")
    private String driverClassName;
}
