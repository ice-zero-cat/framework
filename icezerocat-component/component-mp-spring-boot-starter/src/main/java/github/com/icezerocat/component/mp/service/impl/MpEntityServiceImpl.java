package github.com.icezerocat.component.mp.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.core.exception.ApiException;
import github.com.icezerocat.component.mp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.component.mp.model.MpModel;
import github.com.icezerocat.component.mp.service.BaseMpBuildService;
import github.com.icezerocat.component.mp.service.MpEntityService;
import github.com.icezerocat.component.mp.service.MpService;
import github.com.icezerocat.component.mp.utils.MqPackageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Description: 根据对象进行CRUDService
 * CreateDate:  2021/5/28 15:05
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@Service("mpEntityService")
@SuppressWarnings("unused")
public class MpEntityServiceImpl implements MpEntityService {
    private final BaseMpBuildService baseMpBuildService;
    private final MpService mpService;

    public MpEntityServiceImpl(BaseMpBuildService baseMpBuildService, MpService mpService) {
        this.baseMpBuildService = baseMpBuildService;
        this.mpService = mpService;
    }

    @Override
    public HttpResult<List<?>> retrieve(MpModel mpModel) {
        String entityName = mpModel.getEntityName();
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService;
        try {
            baseMapperObjectNoahService = this.baseMpBuildService.newInstance(MqPackageUtils.getEntityByName(entityName));
        } catch (IllegalAccessException | InstantiationException e) {
            String message = "项目没有此对象".concat(entityName).concat(":").concat(e.getMessage());
            log.error(message);
            e.printStackTrace();
            return HttpResult.Build.<List<?>>getInstance()
                    .setData(Collections.emptyList())
                    .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMsg(message)
                    .setCount(0L)
                    .complete();
        }
        return this.mpService.retrieve(baseMapperObjectNoahService, mpModel);
    }

    @Override
    public boolean deleteBySearch(MpModel mpModel) {
        boolean isDelete = false;
        try {
            NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService =
                    this.baseMpBuildService.newInstance(MqPackageUtils.getEntityByName(mpModel.getEntityName()));
            isDelete = baseMapperObjectNoahService.remove(this.mpService.getWrapper(mpModel));
        } catch (IllegalAccessException | InstantiationException e) {
            log.error("删除失败或构建实体类存在异常！具体原因：".concat(e.getMessage()));
            e.printStackTrace();
        }
        return isDelete;
    }

    @Override
    public List<Object> saveOrUpdateBatch(MpModel mpModel) {
        String entityName = mpModel.getEntityName();
        try {
            Class aClass = MqPackageUtils.getMqClassByName(entityName);
            if (aClass == null) {
                throw new ApiException("项目没有此对象".concat(entityName));
            }
            NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(aClass.newInstance());
            return this.mpService.saveOrUpdateBatch(baseMapperObjectNoahService,mpModel.getObjectList(),aClass);
        } catch (IllegalAccessException | InstantiationException e) {
            log.error("类创建实例出错：{}", e.getMessage());
            e.printStackTrace();
            throw new ApiException("类创建实例出错：".concat(e.getMessage()));
        }
    }
}
