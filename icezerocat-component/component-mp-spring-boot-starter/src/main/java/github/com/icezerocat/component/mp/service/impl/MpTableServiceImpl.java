package github.com.icezerocat.component.mp.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.common.utils.ClassUtils;
import github.com.icezerocat.component.common.utils.StringUtil;
import github.com.icezerocat.component.core.exception.ApiException;
import github.com.icezerocat.component.db.builder.JavassistBuilder;
import github.com.icezerocat.component.mp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.component.mp.model.MpModel;
import github.com.icezerocat.component.mp.service.BaseMpBuildService;
import github.com.icezerocat.component.mp.service.MpService;
import github.com.icezerocat.component.mp.service.MpTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Description: 根据table进行CRUD操作
 * CreateDate:  2021/5/28 15:00
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@Service("mpTableService")
@SuppressWarnings("unused")
public class MpTableServiceImpl implements MpTableService {

    private final BaseMpBuildService baseMpBuildService;
    private final MpService mpService;

    public MpTableServiceImpl(BaseMpBuildService baseMpBuildService, MpService mpService) {
        this.baseMpBuildService = baseMpBuildService;
        this.mpService = mpService;
    }

    @Override
    public List<Object> saveOrUpdateBatch(MpModel mpModel) {
        //service
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(mpModel.getApClassModelBuild());

        //数据转实体类
        String entityName = JavassistBuilder.PACKAGE_NAME + org.springframework.util.StringUtils.capitalize(StringUtil.underline2Camel(mpModel.getApClassModelBuild().getTableName()));
        try {
            Class aClass = ClassUtils.searchClassByClassName(this.baseMpBuildService.getSaveClassPath(), entityName, Thread.currentThread().getContextClassLoader().getParent());
            if (aClass == null) {
                log.error("加载外部对象出错:" + entityName);
                throw new ApiException("加载外部对象出错:" + entityName);
            }
            return this.mpService.saveOrUpdateBatch(baseMapperObjectNoahService, mpModel.getObjectList(), aClass);
        } catch (ClassNotFoundException e) {
            throw new ApiException("找不到类:" + entityName);
        } catch (NoSuchMethodException | MalformedURLException e) {
            throw new ApiException("加载外部对象出错:" + entityName);
        }
    }

    @Override
    public HttpResult<List<?>> retrieve(MpModel mpModel) {
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(mpModel.getApClassModelBuild());
        return this.mpService.retrieve(baseMapperObjectNoahService, mpModel);
    }

    @Override
    public boolean deleteByIds(MpModel mpModel) {
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(mpModel.getApClassModelBuild());
        return baseMapperObjectNoahService.removeByIds(mpModel.getIds());
    }

    @Override
    public boolean deleteBySearch(MpModel mpModel) {
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(mpModel.getApClassModelBuild());
        return baseMapperObjectNoahService.remove(this.mpService.getWrapper(mpModel));
    }
}
