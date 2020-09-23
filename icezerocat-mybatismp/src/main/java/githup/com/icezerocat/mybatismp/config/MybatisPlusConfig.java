package githup.com.icezerocat.mybatismp.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import githup.com.icezerocat.mybatismp.common.mybatisplus.NoahSqlInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ProjectName: [icezero-system]
 * Package:     [com.githup.icezerocat.admin.config.MybatisPlusConfig]
 * Description: mybatis-plus分页插件
 * CreateDate:  2020/4/26 15:23
 *
 * @author 0.0.0
 * @version 1.0
 */
@Configuration
public class MybatisPlusConfig {
    /**
     * 分页拦截器
     *
     * @return 分页拦截器
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        // paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        // paginationInterceptor.setLimit(500);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }

    /**
     * sql注射器
     *
     * @return 注射器
     */
    @Bean
    public NoahSqlInjector logicSqlInjector() {
        return new NoahSqlInjector();
    }
}
