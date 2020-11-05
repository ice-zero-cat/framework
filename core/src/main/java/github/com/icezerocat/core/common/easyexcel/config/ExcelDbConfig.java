package github.com.icezerocat.core.common.easyexcel.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

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
    @Value("${jdbc.url:}")
    private String url;
    /**
     * 数据库用户名
     */
    @Value("${jdbc.username:}")
    private String username;
    /**
     * 数据库用户密码
     */
    @Value("${jdbc.password:}")
    private String password;
    /**
     * 数据库驱动类名
     */
    @Value("${jdbc.driverClassName:}")
    private String driverClassName;

    /**
     * 默认数据库连接地址
     */
    @Value("${spring.datasource.url:}")
    private String springUrl;

    /**
     * 默认数据库用户名
     */
    @Value("${spring.datasource.username:}")
    private String springUsername;

    /**
     * 默认数据库用户密码
     */
    @Value("${spring.datasource.password:}")
    private String sprigPassword;

    /**
     * 默认数据库驱动类
     */
    @Value("${spring.datasource.driver-class-name:}")
    private String sprigDriverClassName;

    public String getUrl() {
        return ObjectUtils.isEmpty(url) ? springUrl : url;
    }

    public String getUsername() {
        return ObjectUtils.isEmpty(username) ? springUsername : username;
    }

    public String getPassword() {
        return ObjectUtils.isEmpty(username) ? sprigPassword : password;
    }

    public String getDriverClassName() {
        return ObjectUtils.isEmpty(driverClassName) ? sprigDriverClassName : driverClassName;
    }
}
