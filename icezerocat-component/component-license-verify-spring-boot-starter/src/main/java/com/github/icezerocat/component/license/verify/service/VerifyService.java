package com.github.icezerocat.component.license.verify.service;

import de.schlichtherle.license.LicenseContent;
import org.springframework.web.multipart.MultipartFile;

/**
 * Description: 验证服务
 * CreateDate:  2021/9/3 14:25
 *
 * @author zero
 * @version 1.0
 */
public interface VerifyService {

    /**
     * 获取证书内容
     *
     * @param licFile 证书文件
     * @return 证书内容
     * @throws Exception 读取证书内容异常
     */
    LicenseContent getLicenseContent(MultipartFile licFile) throws Exception;
}
