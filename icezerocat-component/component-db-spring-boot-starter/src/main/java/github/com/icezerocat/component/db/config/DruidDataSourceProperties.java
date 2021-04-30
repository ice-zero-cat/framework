package github.com.icezerocat.component.db.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import javax.annotation.Resource;

/**
 * ProjectName: [icezero-system]
 * Package:     [com.githup.icezerocat.admin.config.DruidDataSourceProperties]
 * Description: 数据源属性
 * CreateDate:  2020/4/29 14:33
 *
 * @author 0.0.0
 * @version 1.0
 */
@Data
//@Configuration
@PropertySources({
        @PropertySource(value = "classpath:bootstrap.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:bootstrap.yml", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:application.yml", ignoreResourceNotFound = true)
})
@ConfigurationProperties(prefix = "spring.datasource.druid")
public class DruidDataSourceProperties {

    @Resource
    private ExcelDbConfig excelDbConfig;

    /**
     * druid名字
     */
    private String name = "druidDataSource";
    /**
     * 链接驱动
     */
    private String driverClassName;
    /**
     * 链接地址
     */
    private String url;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 初始化大小
     */
    private int initialSize = 1;
    /**
     * 最小链接数
     */
    private int minIdle = 1;
    /**
     * 最大连接数
     */
    private int maxActive = 100;
    /**
     * 获取等待超时时间
     */
    private long maxWait = 60000;
    /**
     * 间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private long timeBetweenEvictionRunsMillis = 60000;
    /**
     * 个连接在池中最小生存的时间，单位是毫秒
     */
    private long minEvictableIdleTimeMillis = 30000;

    //druid recycle Druid的连接回收机制
    /**
     * 超过时间限制是否回收
     */
    private boolean removeAbandoned = true;
    /**
     * 超时时间；单位为秒。180秒=3分钟
     */
    private int removeAbandonedTimeout = 180;
    /**
     * 关闭Abandoned连接时输出错误日志,默认false
     */
    private boolean logAbandoned = true;

    /**
     * 检查数据库连接是否有效
     */
    private String validationQuery = "select 'x'";
    /**
     * 从连接池获取连接后，如果超过被空闲剔除周期，是否做一次连接有效性检查
     */
    private boolean testWhileIdle = true;
    /**
     * 从连接池获取连接后，是否马上执行一次检查
     */
    private boolean testOnBorrow = false;
    /**
     * 归还连接到连接池时是否马上做一次检查
     */
    private boolean testOnReturn = false;
    private boolean poolPreparedStatements = true;
    private int maxOpenPreparedStatements = 50;
    private int maxPoolPreparedStatementPerConnectionSize = 20;
    /**
     * filter:配置监控统计拦截的filters，去掉后监控界面SQL无法进行统计，'wall'用于防火墙
     */
    private String filters = "stat,wall,slf4j,config";

    public String getDriverClassName() {
        return StringUtils.isNotBlank(driverClassName) ? driverClassName : this.excelDbConfig.getSprigDriverClassName();
    }

    public String getUrl() {
        return StringUtils.isNotBlank(url) ? url : this.excelDbConfig.getUrl();
    }

    public String getUsername() {
        return StringUtils.isNotBlank(username) ? username : this.excelDbConfig.getUsername();
    }

    public String getPassword() {
        return StringUtils.isNotBlank(password) ? password : this.excelDbConfig.getPassword();
    }
}
