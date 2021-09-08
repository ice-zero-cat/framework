package com.github.icezerocat.component.license.core.helper;

import com.github.icezerocat.component.license.core.model.LicenseCreatorParam;
import com.github.icezerocat.component.license.core.model.LicenseVerifyManager;
import com.github.icezerocat.component.license.core.model.LicenseVerifyParam;
import de.schlichtherle.license.*;

import javax.security.auth.x500.X500Principal;
import java.util.prefs.Preferences;

/**
 * Description: 证书初始化助手
 * CreateDate:  2021/8/30 20:07
 *
 * @author zero
 * @version 1.0
 */
public class ParamInitHelper {

    /**
     * 证书的发行者和主体字段信息
     */
    private final static X500Principal DEFAULT_HOLDER_AND_ISSUER = new X500Principal("CN=a, OU=a, O=a, L=a, ST=a, C=a");

    /**
     * <p>初始化证书生成参数</p>
     *
     * @param param GxLicenseCreatorParam 生成证书参数
     * @return LicenseParam 私钥创建证书lic参数
     */
    public static LicenseParam initLicenseParam(LicenseCreatorParam param) {
        Preferences preferences = Preferences.userNodeForPackage(LicenseCreator.class);
        /* 设置对证书内容加密的秘钥 */
        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());
        KeyStoreParam privateStoreParam = new DefaultKeyStoreParam(LicenseCreator.class
                , param.getPrivateKeysStorePath()
                , param.getPrivateAlias()
                , param.getStorePass()
                , param.getKeyPass());
        return new DefaultLicenseParam(param.getSubject(), preferences, privateStoreParam, cipherParam);
    }

    /**
     * <p>初始化证书内容信息对象</p>
     *
     * @param param GxLicenseCreatorParam 生成证书参数
     * @return LicenseContent 证书内容
     */
    public static LicenseContent initLicenseContent(LicenseCreatorParam param) {
        LicenseContent licenseContent = new LicenseContent();
        licenseContent.setHolder(DEFAULT_HOLDER_AND_ISSUER);
        licenseContent.setIssuer(DEFAULT_HOLDER_AND_ISSUER);
        /* 设置证书名称 */
        licenseContent.setSubject(param.getSubject());
        /* 设置证书有效期 */
        licenseContent.setIssued(param.getIssuedTime());
        /* 设置证书生效日期 */
        licenseContent.setNotBefore(param.getIssuedTime());
        /* 设置证书失效日期 */
        licenseContent.setNotAfter(param.getExpiryTime());
        /* 设置证书用户类型 */
        licenseContent.setConsumerType(param.getConsumerType());
        /* 设置证书用户数量 */
        licenseContent.setConsumerAmount(param.getConsumerAmount());
        /* 设置证书描述信息 */
        licenseContent.setInfo(param.getDescription());
        /* 设置证书扩展信息（对象 -- 额外的ip、mac、cpu等信息） */
        licenseContent.setExtra(param.getLicenseCheck());
        return licenseContent;
    }

    /**
     * <p>初始化证书生成参数</p>
     *
     * @param param License校验类需要的参数
     * @return LicenseParam 公钥认证参数
     */
    public static LicenseParam initLicenseParam(LicenseVerifyParam param) {
        Preferences preferences = Preferences.userNodeForPackage(LicenseVerifyManager.class);
        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());
        // 参数 1,2 从哪个Class.getResource()获得密钥库;
        // 参数 3 密钥库的别名;
        // 参数 4 密钥库存储密码;
        // 参数 5 密钥库密码
        KeyStoreParam publicStoreParam = new DefaultKeyStoreParam(
                LicenseVerifyManager.class
                /* 公钥库存储路径 */
                , param.getPublicKeysStorePath()
                /* 公匙别名 */
                , param.getPublicAlias()
                /* 公钥库访问密码 */
                , param.getStorePass()
                , null);
        return new DefaultLicenseParam(param.getSubject(), preferences, publicStoreParam, cipherParam);
    }
}
