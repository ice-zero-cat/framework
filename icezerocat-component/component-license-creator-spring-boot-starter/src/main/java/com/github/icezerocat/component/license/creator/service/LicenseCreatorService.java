package com.github.icezerocat.component.license.creator.service;

import com.github.icezerocat.component.license.core.model.LicenseCreatorManager;
import com.github.icezerocat.component.license.core.model.LicenseCreatorParam;
import com.github.icezerocat.component.license.core.model.LicenseResult;
import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.common.utils.DateUtil;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * Description: 证书生成接口实现
 * CreateDate:  2021/8/31 22:21
 *
 * @author zero
 * @version 1.0
 */
@Service
public class LicenseCreatorService {

    /**
     * <p>生成证书</p>
     *
     * @param param 证书创建需要的参数对象
     * @return 证书创建结果
     */
    public HttpResult generateLicense(LicenseCreatorParam param) {
        LicenseCreatorManager licenseCreator = new LicenseCreatorManager(param);
        LicenseResult licenseResult = licenseCreator.generateLicense();
        if (licenseResult.getResult()) {
            String message = MessageFormat.format("证书生成成功，证书有效期：{0} - {1}",
                    DateUtil.formatDateTime(param.getIssuedTime()), DateUtil.formatDateTime(param.getExpiryTime()));
            return HttpResult.ok(message, param);
        } else {
            return HttpResult.error("证书文件生成失败！");
        }
    }

}
