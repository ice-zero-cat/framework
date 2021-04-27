package com.github.icezerocat.component.common.easyexcel.object;

import lombok.Data;

import java.io.Serializable;

/**
 * 导入对象
 *
 * Created by zmj
 * On 2019/11/11.
 *
 * @author 0.0.0
 */
@Data
public class EEImport implements Serializable {
    private static final long serialVersionUID = -4138386776343133876L;
    /**
     * 字段
     */
    private String field;
    /**
     * 值
     */
    private Object value;
    /**
     * 类型
     */
    private String type;
    /**
     * 日期格式
     */
    private String formatDate;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 字段描述
     */
    private String readme;
}
