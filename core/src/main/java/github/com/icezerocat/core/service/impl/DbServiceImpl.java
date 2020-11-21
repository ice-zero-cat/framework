package github.com.icezerocat.core.service.impl;


import com.google.common.collect.ImmutableMap;
import github.com.icezerocat.core.common.easyexcel.object.Table;
import github.com.icezerocat.core.config.DruidConfig;
import github.com.icezerocat.core.service.DbService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DbServiceImpl implements DbService {

    private final DruidConfig druidConfig;

    @Override
    public List<Table> getTableInfo() {
        Connection connection = this.druidConfig.getConnectionByDruid();
        List<Table> tableList = new ArrayList<>();
        //获取数据库的元数据
        DatabaseMetaData dbMetaData;
        Set<String> tableNameSet = new HashSet<>();
        try {
            dbMetaData = connection.getMetaData();
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
        this.dbClose(connection);
        return tableList;
    }

    @Override
    public List<Map<String, String>> getTableFieldBySchema(String tableName) {
        Connection connection = this.druidConfig.getConnectionByDruid();
        List<Map<String, String>> tableFieldList = new ArrayList<>();
        Set<String> columnNameSet = new HashSet<>();
        try {
            ResultSet rs = connection.getMetaData().getColumns(null, getSchema(connection), tableName.toUpperCase(), "%");
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
        this.dbClose(connection);
        return tableFieldList;
    }

    @Override
    public List<Map<String, String>> getTableField(String tableName) {
        Connection connection = this.druidConfig.getConnectionByDruid();
        List<Map<String, String>> tableFieldList = new ArrayList<>();

        String sql = "select * from " + tableName;
        ResultSet resultSet;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
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
        this.dbClose(connection);
        return tableFieldList;
    }

    /**
     * 关闭数据库连接
     *
     * @param connection connection
     */
    private void dbClose(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("数据关闭异常");
            e.printStackTrace();
        }
    }

    /**
     * 其他数据库不需要这个方法 oracle和db2需要
     *
     * @return schema
     * @throws Exception ORACLE数据库模式不允许为空
     */
    private String getSchema(Connection connection) throws Exception {
        String schema;
        schema = connection.getMetaData().getUserName();
        if ((schema == null) || (schema.length() == 0)) {
            throw new Exception("ORACLE数据库模式不允许为空");
        }
        return schema.toUpperCase();
    }

}
