package github.com.icezerocat.backup.service.impl;

import github.com.icezerocat.backup.service.MysqlBackupService;
import github.com.icezerocat.backup.util.MySqlBackupRestoreUtils;
import org.springframework.stereotype.Service;


/**
 * 数据备份实现类
 *
 * @author 0.0.0
 */
@Service
public class MysqlBackupServiceImpl implements MysqlBackupService {

    @Override
    public boolean backup(String host, String userName, String password, String backupFolderPath, String fileName,
                          String database) throws Exception {
        return MySqlBackupRestoreUtils.backup(host, userName, password, backupFolderPath, fileName, database);
    }

    @Override
    public boolean restore(String restoreFilePath, String host, String userName, String password, String database)
            throws Exception {
        return MySqlBackupRestoreUtils.restore(restoreFilePath, host, userName, password, database);
    }

}
