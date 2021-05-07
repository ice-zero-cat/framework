package github.com.icezerocat.mybatismp.page;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ProjectName: [icezero-system]
 * Package:     [PageResult]
 * Description: 分页返回结果
 * CreateDate:  2020/4/26 9:45
 *
 * @author 0.0.0
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class PageResult implements Serializable {

    private static final long serialVersionUID = 7636388861047149340L;

    /**
     * 当前页码
     */
    private int pageNum;
    /**
     * 每页数量
     */
    private int pageSize;
    /**
     * 记录总数
     */
    private long totalSize;
    /**
     * 页码总数
     */
    private int totalPages;
    /**
     * 分页数据
     */
    private List<?> content;

    public static PageResult getPageResult(List<?> content, long total, PageRequest pageRequest) {
        //返回格式
        PageResult pageResult = new PageResult();
        pageResult.setContent(content);
        pageResult.setPageNum(pageRequest.getPageNum());
        pageResult.setPageSize(pageRequest.getPageSize());
        pageResult.setTotalSize(total);
        int totalPages = new BigDecimal((double) pageResult.getTotalSize() / (double) pageResult.getPageSize()).setScale(0, BigDecimal.ROUND_UP).intValue();
        pageResult.setTotalPages(totalPages);
        return pageResult;
    }
}
