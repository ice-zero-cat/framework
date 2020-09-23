package github.com.icezerocat.admin;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 0.0.0
 */
@Slf4j
@MapperScan("com.githup.icezerocat.**.mapper")
@SpringBootApplication(scanBasePackages = {"github.com.icezerocat"})
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        log.debug("admin服务启动成功！代码千万行，注释第一行；命名不规范，同事两行泪");
    }

}
