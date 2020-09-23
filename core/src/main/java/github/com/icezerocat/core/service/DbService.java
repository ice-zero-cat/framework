package github.com.icezerocat.core.service;


import github.com.icezerocat.core.common.easyexcel.object.Table;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 数据库连接service
 * <p>
 * Created by zmj
 * On 2019/12/24.
 *
 * @author 0.0.0
 */
public interface DbService {

    /**
     * 获取链接
     *
     * @return 链接对象
     */
    Connection getConnection();

    /**
     * 链接默认数据库
     *
     * @return 链接结果
     */
    boolean dbDefaultInit();

    /**
     * mysql初始化链接
     *
     * @param ip       ip地址
     * @param port     端口号
     * @param user     用户
     * @param password 密码
     * @param dbName   数据库名
     * @return 链接结果
     */
    boolean mysqlInit(String ip, String port, String user, String password, String dbName);

    /**
     * 关闭链接
     *
     * @return 关闭结果
     */
    String dbClose();

    /**
     * 获取数据库表名信息
     *
     * @return 数据表信息
     */
    List<Table> getTableInfo();

    /**
     * 获取表字段名和类型
     *
     * @param tableName 表名
     * @return 表搜索字段
     */
    List<Map<String, String>> getTableFieldBySchema(String tableName);

    /**
     * 获取表字段名和类型
     *
     * @param tableName 表名
     * @return 表搜索字段
     */
    List<Map<String, String>> getTableField(String tableName);
}
