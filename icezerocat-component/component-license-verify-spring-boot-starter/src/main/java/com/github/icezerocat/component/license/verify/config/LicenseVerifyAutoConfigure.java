package com.github.icezerocat.component.license.verify.config;

import com.github.icezerocat.component.license.core.helper.LoggerHelper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Description: License验证模块自动扫包/装配Bean实例
 * CreateDate:  2021/8/31 23:34
 *
 * @author zero
 * @version 1.0
 */
@Configuration
@ComponentScan(basePackages = {"com.github.icezerocat.component.license.verify"})
public class LicenseVerifyAutoConfigure {

    public LicenseVerifyAutoConfigure(){
        LoggerHelper.info("============ license-verify-spring-boot-starter initialization！ ===========");
    }
}
