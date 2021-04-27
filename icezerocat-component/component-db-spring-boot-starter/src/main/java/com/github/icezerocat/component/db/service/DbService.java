package com.github.icezerocat.component.db.service;


import com.github.icezerocat.component.common.easyexcel.object.Table;

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
