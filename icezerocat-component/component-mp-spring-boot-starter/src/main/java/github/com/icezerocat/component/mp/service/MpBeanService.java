package github.com.icezerocat.component.mp.service;

import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.mp.model.MpModel;

import java.util.List;

/**
 * Description: 根据Bean进行CRUD服务
 * CreateDate:  2021/5/28 14:54
 *
 * @author zero
 * @version 1.0
 */
public interface MpBeanService {

    /**
     * 根据bean获取service，查询数据
     *
     * @param mpModel mp模型
     * @return 数据集合
     */
    HttpResult<List<?>> retrieve(MpModel mpModel);

    /**
     * 根据bean获取Service，根据ids删除数据
     *
     * @param mpModel mp模型
     * @return 删除数量
     */
    boolean deleteByIds(MpModel mpModel);

    /**
     * 批量保存或更新
     *
     * @param mpModel mp模型
     * @return 保存结果（集合为空则保存失败）
     */
    List<Object> saveOrUpdateBatch(MpModel mpModel);
}
