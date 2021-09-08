package com.github.icezerocat.component.license.verify.web.controller;

import com.github.icezerocat.component.license.verify.service.VerifyService;
import de.schlichtherle.license.LicenseContent;
import github.com.icezerocat.component.common.http.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Description: 许可证认证控制器
 * CreateDate:  2021/9/3 11:45
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("license")
public class LicenseVerifyController {

    final private VerifyService verifyService;

    public LicenseVerifyController(VerifyService verifyService) {
        this.verifyService = verifyService;
    }

    /**
     * 获取证书内容
     *
     * @param licFile 证书文件
     * @return 证书内容
     */
    @PostMapping("getLicenseContent")
    public HttpResult getLicenseContent(@RequestParam MultipartFile licFile) {
        try {
            LicenseContent licenseContent = this.verifyService.getLicenseContent(licFile);
            return HttpResult.ok(licenseContent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HttpResult.error(e.getMessage());
        }
    }
}
