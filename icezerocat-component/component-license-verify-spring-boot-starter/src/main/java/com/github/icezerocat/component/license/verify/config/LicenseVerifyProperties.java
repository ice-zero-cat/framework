package com.github.icezerocat.component.license.verify.config;

import com.github.icezerocat.component.license.core.model.LicenseVerifyParam;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Description: License验证属性类
 * CreateDate:  2021/8/31 23:27
 *
 * @author zero
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = "springboot.license.verify")
public class LicenseVerifyProperties {
    /**
     * 主题
     */
    private String subject;
    /**
     * 公钥别名
     */
    private String publicAlias;
    /**
     * 公钥库存储路径
     */
    private String publicKeysStorePath = "";
    /**
     * 访问公钥库的密码
     */
    private String storePass = "";
    /**
     * 证书生成路径
     */
    private String licensePath;
    /**
     * 是否自动加载
     */
    private boolean isAutoLoad;
    /**
     * 是否全局拦截
     */
    private boolean isGlobalLicense;

    public LicenseVerifyProperties() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPublicAlias() {
        return publicAlias;
    }

    public void setPublicAlias(String publicAlias) {
        this.publicAlias = publicAlias;
    }

    public String getPublicKeysStorePath() {
        return publicKeysStorePath;
    }

    public void setPublicKeysStorePath(String publicKeysStorePath) {
        this.publicKeysStorePath = publicKeysStorePath;
    }

    public String getStorePass() {
        return storePass;
    }

    public void setStorePass(String storePass) {
        this.storePass = storePass;
    }

    public String getLicensePath() {
        return licensePath;
    }

    public void setLicensePath(String licensePath) {
        this.licensePath = licensePath;
    }

    public boolean isAutoLoad() {
        return isAutoLoad;
    }

    public void setIsAutoLoad(boolean autoLoad) {
        isAutoLoad = autoLoad;
    }

    public boolean isGlobalLicense() {
        return isGlobalLicense;
    }

    public void setIsGlobalLicense(boolean globalLicense) {
        isGlobalLicense = globalLicense;
    }

    public LicenseVerifyParam getVerifyParam() {
        LicenseVerifyParam param = new LicenseVerifyParam();
        param.setSubject(subject);
        param.setPublicAlias(publicAlias);
        param.setStorePass(storePass);
        param.setLicensePath(licensePath);
        param.setPublicKeysStorePath(publicKeysStorePath);
        return param;
    }
}
