package github.com.icezerocat.component.mp.model;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import github.com.icezerocat.component.common.model.ApClassModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
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
@NoArgsConstructor
@AllArgsConstructor
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
    private List<Map<String, Object>> objectList = new ArrayList<>();

    /**
     * 页码
     */
    private long page = -1;

    /**
     * 页大小
     */
    private long limit = -1;

    /**
     * 搜索数据
     */
    private List<Search> searches = new ArrayList<>();

    /**
     * 排序数据
     */
    List<OrderItem> orders = new ArrayList<>();

    /**
     * ids集合
     */
    Collection<? extends Serializable> ids = new ArrayList<>();

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
