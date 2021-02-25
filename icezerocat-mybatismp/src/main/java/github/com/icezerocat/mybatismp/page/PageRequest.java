package github.com.icezerocat.mybatismp.page;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ProjectName: [icezero-system]
 * Package:     [com.githup.icezerocat.core.PageRequest]
 * Description: 分页请求
 * CreateDate:  2020/4/23 12:03
 *
 * @author 0.0.0
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -3745750799793066971L;
    /**
     * 当前页码
     */
    private int pageNum = 1;
    /**
     * 每页数量
     */
    private int pageSize = 10;
    /**
     * 每页数量
     */
    private Map<String, ColumnFilter> columnFilters = new HashMap<>();
}
