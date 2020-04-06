package github.com.icezerocat.backup.datasource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 数据源
 * @author 0.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "backup.datasource")
public class BackupDataSourceProperties {

    private String host;
    private String userName;
    private String password;
    private String database;
}  