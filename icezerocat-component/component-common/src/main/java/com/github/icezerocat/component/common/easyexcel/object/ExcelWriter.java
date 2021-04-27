package com.github.icezerocat.component.common.easyexcel.object;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 0.0.0
 * ProjectName: [easyexcel]
 * Package: [com.excel.easyexcel.object.EasyExcelWriterObject]
 * Description 导出对象
 * Date 2020/3/13 16:39
 */
@Data
public class ExcelWriter implements Serializable {
    /**
     * 字段名
     */
    private String field;
    /**
     * 数据类型
     */
    private String type;
    /**
     * 字段注解
     */
    private List<FieldAnnotation> fieldAnnotationList;
}
