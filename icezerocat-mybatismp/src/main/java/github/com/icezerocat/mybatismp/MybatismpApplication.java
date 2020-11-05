package github.com.icezerocat.mybatismp;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Description: 启动类
 * CreateDate:  2020/11/4 9:09
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@MapperScan("github.com.icezerocat.**.mapper")
@SpringBootApplication(scanBasePackages = "github.com.icezerocat")
public class MybatismpApplication {
    public static void main(String[] args) {
        SpringApplication.run(MybatismpApplication.class, args);
        log.debug("mp服务启动成功！代码千万行，注释第一行；命名不规范，同事两行泪");
    }
}
