package github.com.icezerocat.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * ProjectName: [icezero-system]
 * Package:     [com.githup.icezerocat.admin.config.DruidConfig]
 * Description: Druid数据源配置
 * CreateDate:  2020/4/29 14:39
 *
 * @author 0.0.0
 * @version 1.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DruidDataSourceProperties.class)
public class DruidConfig {

    private static DataSource dataSource;

    @Resource
    private DruidDataSourceProperties druidDataSourceProperties;

    @Bean
    @ConditionalOnMissingBean
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setName(druidDataSourceProperties.getName());
        druidDataSource.setDriverClassName(druidDataSourceProperties.getDriverClassName());
        druidDataSource.setUrl(druidDataSourceProperties.getUrl());
        druidDataSource.setUsername(druidDataSourceProperties.getUsername());
        druidDataSource.setPassword(druidDataSourceProperties.getPassword());
        druidDataSource.setDriverClassName(druidDataSourceProperties.getDriverClassName());
        druidDataSource.setInitialSize(druidDataSourceProperties.getInitialSize());
        druidDataSource.setMinIdle(druidDataSourceProperties.getMinIdle());
        druidDataSource.setMaxActive(druidDataSourceProperties.getMaxActive());
        druidDataSource.setMaxWait(druidDataSourceProperties.getMaxWait());
        druidDataSource.setTimeBetweenEvictionRunsMillis(druidDataSourceProperties.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(druidDataSourceProperties.getMinEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(druidDataSourceProperties.getValidationQuery());
        druidDataSource.setTestWhileIdle(druidDataSourceProperties.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(druidDataSourceProperties.isTestOnBorrow());
        druidDataSource.setTestOnReturn(druidDataSourceProperties.isTestOnReturn());
        druidDataSource.setPoolPreparedStatements(druidDataSourceProperties.isPoolPreparedStatements());
        druidDataSource.setMaxOpenPreparedStatements(druidDataSourceProperties.getMaxOpenPreparedStatements());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(druidDataSourceProperties.getMaxPoolPreparedStatementPerConnectionSize());
        //回收机制
        druidDataSource.setRemoveAbandoned(druidDataSourceProperties.isRemoveAbandoned());
        druidDataSource.setRemoveAbandonedTimeout(druidDataSourceProperties.getRemoveAbandonedTimeout());
        druidDataSource.setLogAbandoned(druidDataSource.isLogAbandoned());

        try {
            druidDataSource.setFilters(druidDataSourceProperties.getFilters());
            log.debug("druidDataSource:{}", druidDataSource);
            druidDataSource.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DruidConfig.dataSource = druidDataSource;
        return druidDataSource;
    }

    /**
     * 注册Servlet信息， 配置监控视图(相当于Web Servlet配置)
     *
     * @return Servlet注册Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ServletRegistrationBean<Servlet> druidServlet() {
        ServletRegistrationBean<Servlet> servletRegistrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");

        //白名单：servletRegistrationBean.addInitParameter("allow", "192.168.6.195");
        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
        servletRegistrationBean.addInitParameter("deny", "192.168.6.73");
        //登录查看信息的账号密码, 用于登录Druid监控后台
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        //是否能够重置数据.
        servletRegistrationBean.addInitParameter("resetEnable", "true");
        return servletRegistrationBean;

    }

    /**
     * 注册Filter信息, 监控拦截器(相当于Web Filter配置)
     *
     * @return 筛选注册Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<Filter> filterRegistrationBean() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    /**
     * 通过连接池获取sql connection
     *
     * @return sql链接
     */
    public Connection getConnectionByDruid() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("数据池获取connection失败：{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
