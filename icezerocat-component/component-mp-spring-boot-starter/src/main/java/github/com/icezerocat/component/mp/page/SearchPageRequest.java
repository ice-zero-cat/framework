package github.com.icezerocat.component.mp.page;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import github.com.icezerocat.component.mp.model.Search;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description: 搜索分页请求
 * CreateDate:  2020/11/19 8:56
 *
 * @author zero
 * @version 1.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchPageRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -7833985608007709475L;

    /**
     * 当前页码
     */
    private int pageNum = -1;

    /**
     * 每页数量
     */
    private int pageSize = -1;

    /**
     * 搜索
     */
    private List<Search> searches = Collections.emptyList();

    private List<OrderItem> orders = new ArrayList<>();
}
