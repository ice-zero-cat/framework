package github.com.icezerocat.component.db.config;

import github.com.icezerocat.component.db.service.ClassService;
import github.com.icezerocat.component.db.service.DbService;
import github.com.icezerocat.component.db.service.impl.ClassServiceImpl;
import github.com.icezerocat.component.db.service.impl.DbServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Description: db自动装载
 * CreateDate:  2021/5/6 9:42
 *
 * @author zero
 * @version 1.0
 */
public class ZeroDbAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    public DbService dbService(DruidConfig druidConfig) {
        return new DbServiceImpl(druidConfig);
    }

    @Bean
    @ConditionalOnBean(DbService.class)
    @ConditionalOnMissingBean
    public ClassService classService(DbService dbService) {
        return new ClassServiceImpl(dbService);
    }
}
