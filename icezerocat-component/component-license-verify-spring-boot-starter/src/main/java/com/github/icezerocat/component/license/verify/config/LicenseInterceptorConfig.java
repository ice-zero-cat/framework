package com.github.icezerocat.component.license.verify.config;

import com.github.icezerocat.component.license.verify.interceptor.LicenseVerifyInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Description: License拦截器配置类
 * CreateDate:  2021/8/31 23:20
 *
 * @author zero
 * @version 1.0
 */
@Configuration
public class LicenseInterceptorConfig implements WebMvcConfigurer {

    @Bean
    public LicenseVerifyInterceptor getLicenseCheckInterceptor() {
        return new LicenseVerifyInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.getLicenseCheckInterceptor()).addPathPatterns("/**");
    }
}
