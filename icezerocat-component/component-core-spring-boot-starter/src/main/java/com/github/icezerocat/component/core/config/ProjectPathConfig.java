package com.github.icezerocat.component.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by zmj
 * On 2020/2/28.
 *
 * @author 0.0.0
 */
public class ProjectPathConfig implements WebMvcConfigurer {

    private Logger log = LoggerFactory.getLogger(ProjectPathConfig.class);

    public static String PROJECT_PATH = "";

    /**
     * 虚拟路径访问配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            PROJECT_PATH = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/";
        } else {
            //"/home/data1/source/factory-boot-master/factory-play-single"
            String systemPath = System.getProperty("user.dir");
            PROJECT_PATH = "/".equals(systemPath) ? systemPath : systemPath + "/";
        }
        log.info("project-path:{}", PROJECT_PATH);
    }
}
