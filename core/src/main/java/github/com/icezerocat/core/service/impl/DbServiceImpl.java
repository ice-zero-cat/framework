package github.com.icezerocat.core.service.impl;


import com.google.common.collect.ImmutableMap;
import github.com.icezerocat.core.common.easyexcel.config.ExcelDbConfig;
import github.com.icezerocat.core.common.easyexcel.object.Table;
import github.com.icezerocat.core.service.DbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

/**
 * 数据库连接service
 * <p>
 * Created by zmj
 * On 2019/12/24.
 *
 * @author 0.0.0
 */
@Slf4j
@Service
public class DbServiceImpl implements DbService {

    /**
     * 数据库链接
     */
    private Connection connection;
    private final ExcelDbConfig excelDbConfig;

    public DbServiceImpl(ExcelDbConfig excelDbConfig) {
        this.excelDbConfig = excelDbConfig;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public boolean dbDefaultInit() {
        return excelDbConfig != null && this.connectionDb(excelDbConfig.getDriverClassName(), excelDbConfig.getUrl(), excelDbConfig.getUsername(), excelDbConfig.getPassword());
    }

    @Override
    public boolean mysqlInit(String ip, String port, String user, String password, String dbName) {
        //驱动程序名
        String driver = "com.mysql.cj.jdbc.Driver";
        //数据库服务器
        //URL指向要访问的数据库名
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai";
        return connectionDb(driver, url, user, password);
    }

    @Override
    public String dbClose() {
        try {
            this.connection.close();
            this.connection = null;
            return "关闭数据库连接成功";
        } catch (SQLException e) {
            log.error("数据关闭异常");
            e.printStackTrace();
            return "数据关闭异常";
        }
    }

    @Override
    public List<Table> getTableInfo() {
        this.dbDefaultInit();
        List<Table> tableList = new ArrayList<>();
        //获取数据库的元数据
        DatabaseMetaData dbMetaData;
        Set<String> tableNameSet = new HashSet<>();
        try {
            dbMetaData = this.connection.getMetaData();
            //从元数据中获取到所有的表名
            ResultSet resultSet = dbMetaData.getTables(null, null, null, new String[]{"TABLE"});
            while (resultSet.next()) {
                Table table = new Table();
                String dbTableName = resultSet.getString(Table.TABLE_NAME);
                //重复则跳出
                if (!tableNameSet.add(dbTableName)) {
                    break;
                }
                table.setName(dbTableName);
                table.setType(resultSet.getString(Table.TABLE_TYPE));
                table.setCat(resultSet.getString(Table.TABLE_CAT));
                table.setSchem(resultSet.getString(Table.TABLE_SCHEM));
                table.setRemarks(resultSet.getString(Table.REMARKS));
                tableList.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.dbClose();
        return tableList;
    }

    @Override
    public List<Map<String, String>> getTableFieldBySchema(String tableName) {
        this.dbDefaultInit();
        List<Map<String, String>> tableFieldList = new ArrayList<>();
        Set<String> columnNameSet = new HashSet<>();
        try {
            ResultSet rs = connection.getMetaData().getColumns(null, getSchema(), tableName.toUpperCase(), "%");
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                //重复则跳出
                if (!columnNameSet.add(columnName)) {
                    break;
                }
                String remarks = rs.getString("REMARKS");
                String dbType = rs.getString("TYPE_NAME");
                if (remarks == null || "".equals(remarks)) {
                    remarks = columnName;
                }
                tableFieldList.add(ImmutableMap.of(
                        Table.FIELD, columnName,
                        Table.FIELDTYPE, dbType,
                        Table.REMARKS, remarks
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.dbClose();
        return tableFieldList;
    }

    @Override
    public List<Map<String, String>> getTableField(String tableName) {
        this.dbDefaultInit();
        List<Map<String, String>> tableFieldList = new ArrayList<>();

        String sql = "select * from " + tableName;
        ResultSet resultSet;
        try {
            PreparedStatement ps = this.connection.prepareStatement(sql);
            resultSet = ps.executeQuery();
            //结果集元数据
            ResultSetMetaData meta = resultSet.getMetaData();
            //表列数量
            int columnCount = meta.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableFieldList.add(ImmutableMap.of(
                        Table.FIELD, meta.getColumnName(i),
                        Table.FIELDTYPE, meta.getColumnTypeName(i),
                        Table.FIELD_ISNOTABLE, String.valueOf(meta.isNullable(i))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.dbClose();
        return tableFieldList;
    }

    /**
     * 链接数据库
     *
     * @param driver   驱动
     * @param url      数据库地址
     * @param user     用户名
     * @param password 密码
     * @return 链接结果
     */
    private boolean connectionDb(String driver, String url, String user, String password) {
        try {
            Class.forName(driver);
            //声明Connection对象
            if (this.connection == null) {
                this.connection = DriverManager.getConnection(url, user, password);
            }
            log.debug("数据库连接成功：{}", this.connection);
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            log.error("数据库连接失败");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 其他数据库不需要这个方法 oracle和db2需要
     *
     * @return schema
     * @throws Exception ORACLE数据库模式不允许为空
     */
    private String getSchema() throws Exception {
        String schema;
        schema = this.connection.getMetaData().getUserName();
        if ((schema == null) || (schema.length() == 0)) {
            throw new Exception("ORACLE数据库模式不允许为空");
        }
        return schema.toUpperCase();
    }

}
