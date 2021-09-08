package com.github.icezerocat.component.license.verify.service.impl;

import com.github.icezerocat.component.license.core.model.LicenseCustomManager;
import com.github.icezerocat.component.license.core.model.LicenseVerifyManager;
import com.github.icezerocat.component.license.verify.service.VerifyService;
import de.schlichtherle.license.LicenseContent;
import github.com.icezerocat.component.common.utils.MultipartFileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Description: 验证服务
 * CreateDate:  2021/9/3 14:26
 *
 * @author zero
 * @version 1.0
 */
@Service
public class VerifyServiceImpl implements VerifyService {

    @Override
    public LicenseContent getLicenseContent(MultipartFile licFile) throws Exception {
        File file = MultipartFileUtil.multipartFileToFile(licFile);
        LicenseCustomManager licenseManager = LicenseVerifyManager.licenseManager;
        return licenseManager.getLicenseContent(file);
    }
}
