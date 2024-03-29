package com.github.icezerocat.component.license.core.model;


import com.github.icezerocat.component.license.core.helper.LoggerHelper;
import com.github.icezerocat.component.license.core.helper.ParamInitHelper;
import de.schlichtherle.license.*;
import github.com.icezerocat.component.common.utils.DateUtil;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

/**
 * Description: License校验类
 * CreateDate:  2021/8/30 19:58
 *
 * @author zero
 * @version 1.0
 */
public class LicenseVerifyManager {

    public static LicenseCustomManager licenseManager;

    /**
     * <p>安装License证书</p>
     *
     * @param param License校验类需要的参数
     * @return LicenseResult 验证结果
     */
    public synchronized LicenseResult install(LicenseVerifyParam param) {
        try {
            /* 1、初始化License证书参数 */
            LicenseParam licenseParam = ParamInitHelper.initLicenseParam(param);
            /* 2、创建License证书管理器对象 */
//          LicenseManager licenseManager =new LicenseManager(licenseParam);
            //走自定义的Lic管理
            licenseManager = new LicenseCustomManager(licenseParam);
            /* 3、获取要安装的证书文件 */
            File licenseFile = ResourceUtils.getFile(param.getLicensePath());
            /* 4、如果之前安装过证书，先卸载之前的证书 == 给null */
            licenseManager.uninstall();
            /* 5、开始安装 */
            LicenseContent content = licenseManager.install(licenseFile);
            String message = MessageFormat.format("证书安装成功，证书有效期：{0} - {1}",
                    DateUtil.formatDateTime(content.getNotBefore()), DateUtil.formatDateTime(content.getNotAfter()));
            LoggerHelper.info(message);
            return new LicenseResult(message, content);
        } catch (LicenseContentException contentExc) {
            String message = contentExc.getMessage();
            LoggerHelper.error(message);
            return new LicenseResult(false, message, contentExc);
        } catch (Exception e) {
            LoggerHelper.error(e.getMessage(), e);
            return new LicenseResult(false, e.getMessage(), e);
        }
    }

    /**
     * <p>校验License证书</p>
     *
     * @param param License校验类需要的参数
     * @return LicenseResult 验证结果
     */
    public LicenseResult verify(LicenseVerifyParam param) {

        /* 1、初始化License证书参数 */
        LicenseParam licenseParam = ParamInitHelper.initLicenseParam(param);
        /* 2、创建License证书管理器对象 */
        LicenseManager licenseManager = new LicenseCustomManager(licenseParam);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        /* 3、开始校验证书 */
        try {
            LicenseContent licenseContent = licenseManager.verify();
            String message = MessageFormat.format("证书校验通过，证书有效期：{0} - {1}",
                    format.format(licenseContent.getNotBefore()), format.format(licenseContent.getNotAfter()));
            LoggerHelper.info(message);
            return new LicenseResult(message, licenseContent);
        } catch (NoLicenseInstalledException ex) {
            String message = "证书未安装！";
            LoggerHelper.error(message, ex);
            return new LicenseResult(false, message, ex);
        } catch (LicenseContentException cex) {
            LoggerHelper.error(cex.getMessage(), cex);
            return new LicenseResult(false, cex.getMessage(), cex);
        } catch (Exception e) {
            String message = "证书校验失败！";
            LoggerHelper.error(message, e);
            return new LicenseResult(false, message, e);
        }
    }


}
