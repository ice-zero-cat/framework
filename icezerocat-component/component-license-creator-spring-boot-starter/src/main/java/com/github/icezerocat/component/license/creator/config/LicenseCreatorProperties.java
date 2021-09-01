package com.github.icezerocat.component.license.creator.config;

import github.com.icezerocat.component.core.exception.ApiException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.text.MessageFormat;

/**
 * Description: License生成配置类
 * CreateDate:  2021/8/31 22:05
 *
 * @author zero
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "springboot.license.generate")
public class LicenseCreatorProperties {
    /**
     * 证书生成临时存放路径
     */
    private String tempPath;

    public LicenseCreatorProperties() {
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
        File file = new File(tempPath);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                throw new ApiException(MessageFormat.format("创建文件失败：{0}", tempPath));
            }
        }
    }
}
