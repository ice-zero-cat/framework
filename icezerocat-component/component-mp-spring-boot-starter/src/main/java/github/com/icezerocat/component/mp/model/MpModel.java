package github.com.icezerocat.component.mp.model;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import github.com.icezerocat.component.common.model.ApClassModel;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: mp模型
 * CreateDate:  2021/5/11 21:46
 *
 * @author zero
 * @version 1.0
 */
@Data
@Builder
public class MpModel implements Serializable {

    /**
     * serviceBean名
     */
    private String beanName;

    /**
     * 对象名
     * <p>
     * {@link github.com.icezerocat.component.mp.base.BaseMpEntity 必须继承此类}
     */
    private String entityName;

    /**
     * 实体类数据
     */
    private List<Map<String, Object>> objectList;

    /**
     * 页码
     */
    private long page;

    /**
     * 页大小
     */
    private long limit;

    /**
     * 搜索数据
     */
    private List<Search> searches;

    /**
     * 排序数据
     */
    List<OrderItem> orders;

    /**
     * ids集合
     */
    Collection<? extends Serializable> ids;

    /**
     * 自定义ApClass构建
     */
    private ApClassModel.Build apClassModelBuild;

    /**
     * service名字
     */
    private String serviceName;

    /**
     * service方法
     */
    private String serviceMethod;
}
