package com.github.icezerocat.component.common.easyexcel.object;

import lombok.Data;

/**
 * Created by zmj
 * On 2019/12/24.
 *
 * @author 0.0.0
 */
@Data
public class Table {
    public static final String TABLE_NAME = "TABLE_NAME";
    public static final String TABLE_TYPE = "TABLE_TYPE";
    public static final String TABLE_CAT = "TABLE_CAT";
    public static final String TABLE_SCHEM = "TABLE_SCHEM";
    public static final String REMARKS = "REMARKS";
    public static final String FIELD = "FIELD";
    public static final String FIELDTYPE = "FIELDTYPE";
    /**
     * 字段是否为空
     */
    public static final String FIELD_ISNOTABLE = "FIELD_ISNOTABLE";

    /**
     * 表名
     */
    private String name;
    /**
     * 表类型
     */
    private String type;
    /**
     * 表所属数据库
     */
    private String cat;
    /**
     * 表所属用户名
     */
    private String schem;
    /**
     * 表备注
     */
    private String remarks;

    /**
     * 字段名
     */
    private String field;

    /**
     * 字段类型
     */
    private String fieldType;
}
