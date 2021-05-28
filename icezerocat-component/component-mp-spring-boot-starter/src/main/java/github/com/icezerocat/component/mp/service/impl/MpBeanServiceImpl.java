package github.com.icezerocat.component.mp.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.core.exception.ApiException;
import github.com.icezerocat.component.mp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.component.mp.config.MpApplicationContextHelper;
import github.com.icezerocat.component.mp.model.MpModel;
import github.com.icezerocat.component.mp.service.MpBeanService;
import github.com.icezerocat.component.mp.service.MpService;
import github.com.icezerocat.component.mp.utils.PackageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description: 根据Bean进行CRUD服务
 * CreateDate:  2021/5/28 15:33
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@Service("mpBeanService")
public class MpBeanServiceImpl implements MpBeanService {

    /**
     * 声明对象包名
     */
    @Value("${entity.package:}")
    private String traversePackage;
    /**
     * 包名缓存
     */
    private static Map<String, String> packageName;

    private final MpService mpService;

    public MpBeanServiceImpl(MpService mpService) {
        this.mpService = mpService;
    }

    @Override
    public HttpResult<List<?>> retrieve(MpModel mpModel) {
        @SuppressWarnings("all")
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = (NoahServiceImpl<BaseMapper<Object>, Object>) MpApplicationContextHelper.getBean(MpApplicationContextHelper.getBeanName(mpModel.getBeanName()));
        return this.mpService.retrieve(baseMapperObjectNoahService, mpModel);
    }

    @Override
    public boolean deleteByIds(MpModel mpModel) {
        @SuppressWarnings("all")
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = (NoahServiceImpl<BaseMapper<Object>, Object>) MpApplicationContextHelper.getBean(MpApplicationContextHelper.getBeanName(mpModel.getBeanName()));
        return baseMapperObjectNoahService.removeByIds(mpModel.getIds());
    }

    @Override
    public List<Object> saveOrUpdateBatch(MpModel mpModel) {
        String entityName = org.springframework.util.StringUtils.uncapitalize(mpModel.getEntityName());
        String beanName = MpApplicationContextHelper.getBeanName(mpModel.getBeanName());
        @SuppressWarnings("all")
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = (NoahServiceImpl<BaseMapper<Object>, Object>) MpApplicationContextHelper.getBean(org.springframework.util.StringUtils.uncapitalize(beanName));
        try {
            if (packageName == null && ObjectUtils.isNotEmpty(traversePackage)) {
                packageName = PackageUtil.getClassNameMap(traversePackage);
            }
            String fullPackageName = org.springframework.util.StringUtils.capitalize(entityName);
            if (!packageName.containsKey(fullPackageName)) {
                throw new ApiException("找不到对象名【" + entityName + "】，请尝试全路径包名");
            }
            Class<?> aClass = Class.forName(packageName.get(fullPackageName));
            return this.mpService.saveOrUpdateBatch(baseMapperObjectNoahService, mpModel.getObjectList(), aClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ApiException("找不到类：".concat(e.getMessage()));
        }
    }
}
