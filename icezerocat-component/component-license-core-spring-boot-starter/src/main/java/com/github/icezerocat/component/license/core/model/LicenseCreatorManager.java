package com.github.icezerocat.component.license.core.model;


import com.github.icezerocat.component.license.core.helper.LoggerHelper;
import com.github.icezerocat.component.license.core.helper.ParamInitHelper;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;
import github.com.icezerocat.component.core.exception.ApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;

/**
 * Description: 系统软件证书生成管理器
 * CreateDate:  2021/8/30 19:58
 *
 * @author zero
 * @version 1.0
 */
public class LicenseCreatorManager {

    private LicenseCreatorParam param;

    public LicenseCreatorManager(LicenseCreatorParam param) {
        this.param = param;
    }

    /**
     * <p>生成License证书</p>
     *
     * @return GxLicenseResult 证书生成结果
     */
    public LicenseResult generateLicense() {
        try {
            // 1、根据外部传入的创建Lic的参数信息初始化lic参数（秘钥部分）
            LicenseParam licenseParam = ParamInitHelper.initLicenseParam(param);
            // 2、根据外部传入的创建Lic的属性信息初始化lic内容（除了truelicense自带的还包括自己定义的）
            LicenseContent licenseContent = ParamInitHelper.initLicenseContent(param);
            // 3、构建Lic管理器
            LicenseManager licenseManager = new LicenseCustomManager(licenseParam);
            // 4、根据param传入的lic生成的路径创建空文件
            File licenseFile = new File(this.param.getLicensePath());
            // 5、通过Lic管理器，将内容写入Lic文件中
            licenseManager.store(licenseContent, licenseFile);
            return new LicenseResult("证书生成成功！", licenseContent);
        } catch (Exception e) {
            LoggerHelper.error(e.getMessage());
            String message = MessageFormat.format("证书生成失败！：{0}", param);
            LoggerHelper.error(message, e);
            return new LicenseResult(message, e);
        }
    }

    /**
     * <p>下载License证书</p>
     *
     * @return InputStream 证书文件输入流
     * @throws Exception 证书下载失败
     */
    public InputStream download() throws Exception {
        try {
            LicenseParam licenseParam = ParamInitHelper.initLicenseParam(param);
            LicenseContent licenseContent = ParamInitHelper.initLicenseContent(param);
            LicenseManager licenseManager = new LicenseCustomManager(licenseParam);
            File licenseFile = new File(param.getLicensePath());
            licenseManager.store(licenseContent, licenseFile);
            return new FileInputStream(licenseFile);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerHelper.error(e.getMessage());
            LoggerHelper.error(MessageFormat.format("证书下载失败：{0}", param), e);
            throw new ApiException(e.getMessage());
        }
    }

}
