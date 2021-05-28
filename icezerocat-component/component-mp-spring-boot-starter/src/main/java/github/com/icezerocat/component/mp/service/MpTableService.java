package github.com.icezerocat.component.mp.service;

import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.mp.model.MpModel;

import java.util.List;

/**
 * Description: 根据table进行CRUD操作
 * CreateDate:  2021/5/28 14:54
 *
 * @author zero
 * @version 1.0
 */
public interface MpTableService {

    /**
     * 通过表单名批量保存或更新数据
     *
     * @param mpModel mp模型
     * @return 保存结果：空集合则保存失败
     */
    List<Object> saveOrUpdateBatch(MpModel mpModel);

    /**
     * 通过表名查询List数据（支持分页、单条、总数数据）
     *
     * @param mpModel mp模型对象
     * @return 查询结果
     */
    HttpResult<List<?>> retrieve(MpModel mpModel);

    /**
     * 根据表名和主键ID删除数据
     *
     * @param mpModel mp模型对象
     * @return 删除结果
     */
    boolean deleteByIds(MpModel mpModel);

    /**
     * 根据表名和搜索条件删除数据
     *
     * @param mpModel mp模型对象
     * @return 删除结果
     */
    boolean deleteBySearch(MpModel mpModel);
}
