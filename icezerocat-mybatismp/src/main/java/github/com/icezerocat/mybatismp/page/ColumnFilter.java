package github.com.icezerocat.mybatismp.page;

import java.io.Serializable;

/**
 * ProjectName: [icezero-system]
 * Package:     [ColumnFilter]
 * Description: 分页查询列过滤器
 * CreateDate:  2020/4/23 12:04
 *
 * @author 0.0.0
 * @version 1.0
 */
public class ColumnFilter implements Serializable {
    private static final long serialVersionUID = 7432358835927469966L;
    /**
     * 过滤列名
     */
    private String name;
    /**
     * 查询的值
     */
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
