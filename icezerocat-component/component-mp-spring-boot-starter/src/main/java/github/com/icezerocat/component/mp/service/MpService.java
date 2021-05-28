package github.com.icezerocat.component.mp.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.mp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.component.mp.model.MpModel;
import github.com.icezerocat.component.mp.model.MpResult;

import java.util.List;
import java.util.Map;

/**
 * Description: mp接口服务
 * CreateDate:  2021/5/11 21:43
 *
 * @author zero
 * @version 1.0
 */
public interface MpService {

    /**
     * 反射调用方法
     *
     * @param mpModel mp模型对象
     * @return mpResult
     */
    <T> MpResult<T> invoke(MpModel mpModel);

    /**
     * 查询
     *
     * @param baseMapperObjectNoahService service
     * @param mpModel                     mp模型
     * @return 查询结果
     */
    HttpResult<List<?>> retrieve(NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService, MpModel mpModel);

    /**
     * 批量保存或更新
     *
     * @param baseMapperObjectNoahService
     * @param mapList
     * @param aClass
     * @return
     */
    List<Object> saveOrUpdateBatch(NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService, List<Map<String, Object>> mapList, Class aClass);
}
