package com.github.icezerocat.component.license.verify.listener;

import com.github.icezerocat.component.license.core.helper.LoggerHelper;
import com.github.icezerocat.component.license.core.model.LicenseResult;
import com.github.icezerocat.component.license.core.model.LicenseVerifyManager;
import com.github.icezerocat.component.license.verify.config.LicenseVerifyProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;

/**
 * Description: 项目启动时安装证书
 * 定时检测lic变化，自动更替lic
 * CreateDate:  2021/8/31 23:26
 *
 * @author zero
 * @version 1.0
 */
@Component
public class LicenseVerifyListener implements CommandLineRunner {

    @Resource
    private LicenseVerifyProperties licenseVerifyProperties;

    /**
     * 文件唯一身份标识 == 相当于人类的指纹一样
     */
    private static String md5 = "";

    @Override
    public void run(String... args) {
        if (StringUtils.isNotEmpty(licenseVerifyProperties.getLicensePath())) {
            install();
            try {
                String readMd5 = getMd5(licenseVerifyProperties.getLicensePath());
                if (LicenseVerifyListener.md5 == null || "".equals(LicenseVerifyListener.md5)) {
                    LicenseVerifyListener.md5 = readMd5;
                }
            } catch (Exception ignored) {

            }
        }
    }

    /**
     * 5秒检测一次，不能太快也不能太慢
     *
     * @throws Exception 自动更新lic文件异常
     */
    @Scheduled(cron = "0/5 * * * * ?")
    protected void timer() throws Exception {
        if (!this.licenseVerifyProperties.isAutoLoad()) {
            return;
        }
        String readMd5 = getMd5(licenseVerifyProperties.getLicensePath());
        // 不相等，说明lic变化了
        if (!readMd5.equals(LicenseVerifyListener.md5)) {
            install();
            LicenseVerifyListener.md5 = readMd5;
        }
    }

    /**
     * 安装证书
     */
    private void install() {
        LoggerHelper.info("++++++++ 开始安装证书 ++++++++");
        LicenseVerifyManager licenseVerifyManager = new LicenseVerifyManager();
        /* 走定义校验证书并安装 */
        LicenseResult result = licenseVerifyManager.install(licenseVerifyProperties.getVerifyParam());
        if (result.getResult()) {
            LoggerHelper.info("++++++++ 证书安装成功 ++++++++");
        } else {
            LoggerHelper.info("++++++++ 证书安装失败 ++++++++");
        }
    }

    /**
     * 获取文件的md5
     *
     * @param filePath 文件路径
     * @return md5
     * @throws Exception 读取文件异常
     */
    private String getMd5(String filePath) throws Exception {
        File file;
        String md5 = "";
        try {
            file = ResourceUtils.getFile(filePath);
            if (file.exists()) {
                FileInputStream is = new FileInputStream(file);
                byte[] data = new byte[is.available()];
                int fileSize = is.read(data);
                LoggerHelper.debug(MessageFormat.format("获取文件大小：{0}", fileSize));
                md5 = DigestUtils.md5DigestAsHex(data);
                is.close();
            }
        } catch (FileNotFoundException ignored) {
        }
        return md5;
    }

    /**
     * 比较许可证lic
     *
     * @param filePath 文件路径
     * @return 比较结果
     * @throws Exception io异常
     */
    public boolean equalsLicense(String filePath) throws Exception {
        return md5.equals(getMd5(filePath));
    }

    /**
     * 比较许可证lic
     *
     * @param file 文件
     * @return 比较结果
     * @throws Exception io异常
     */
    public boolean equalsLicense(File file) throws Exception {
        String fileMd5 = "fileMd5";
        if (file.exists()) {
            FileInputStream is = new FileInputStream(file);
            byte[] data = new byte[is.available()];
            int fileSize = is.read(data);
            LoggerHelper.debug(MessageFormat.format("获取文件大小：{0}", fileSize));
            fileMd5 = DigestUtils.md5DigestAsHex(data);
            is.close();
        }
        return md5.equals(fileMd5);
    }
}
