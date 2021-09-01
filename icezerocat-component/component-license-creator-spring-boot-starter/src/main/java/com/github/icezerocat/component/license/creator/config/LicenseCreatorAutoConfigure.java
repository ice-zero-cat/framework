package com.github.icezerocat.component.license.creator.config;

import com.github.icezerocat.component.license.core.helper.LoggerHelper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Description: License生成模块自动扫包/装配Bean实例
 * CreateDate:  2021/8/31 22:04
 *
 * @author zero
 * @version 1.0
 */
@Configuration
@ComponentScan(basePackages = {"com.github.icezerocat.component.license.creator"})
public class LicenseCreatorAutoConfigure {
    public LicenseCreatorAutoConfigure() {
        LoggerHelper.info("============ license-creator-spring-boot-starter initialization！ ===========");
    }
}
