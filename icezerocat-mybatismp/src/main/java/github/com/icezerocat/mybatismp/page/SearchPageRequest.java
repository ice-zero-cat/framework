package github.com.icezerocat.mybatismp.page;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import github.com.icezerocat.mybatismp.model.Search;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("SearchPageRequest-搜索分页请求")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchPageRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -7833985608007709475L;

    /**
     * 当前页码
     */
    @ApiModelProperty("当前页码")
    private int pageNum = -1;

    /**
     * 每页数量
     */
    @ApiModelProperty("每页数量")
    private int pageSize = -1;

    /**
     * 搜索
     */
    @ApiModelProperty("搜索")
    private List<Search> searches = Collections.emptyList();

    @ApiModelProperty("排序")
    private List<OrderItem> orders = new ArrayList<>();
}
