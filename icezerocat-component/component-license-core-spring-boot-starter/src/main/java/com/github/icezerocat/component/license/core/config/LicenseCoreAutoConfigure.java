package com.github.icezerocat.component.license.core.config;

import com.github.icezerocat.component.license.core.helper.LoggerHelper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Description: license-core模块中的Bean实现自动装配 -- 配置类
 * CreateDate:  2021/8/30 19:56
 *
 * @author zero
 * @version 1.0
 */
@Configuration
@ComponentScan(basePackages = {"com.github.icezerocat.component.license.core"})
public class LicenseCoreAutoConfigure {
    public LicenseCoreAutoConfigure() {
        LoggerHelper.info("============ license-core-spring-boot-starter initialization！ ===========");
    }
}
