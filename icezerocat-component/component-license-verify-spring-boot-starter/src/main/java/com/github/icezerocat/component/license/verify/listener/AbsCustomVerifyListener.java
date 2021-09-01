package com.github.icezerocat.component.license.verify.listener;

import com.github.icezerocat.component.license.core.model.LicenseExtraParam;
import github.com.icezerocat.component.core.exception.ApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 增加业务系统中自定义证书验证监听器
 * CreateDate:  2021/8/31 23:25
 *
 * @author zero
 * @version 1.0
 */
public abstract class AbsCustomVerifyListener {
    /**
     * 软件证书参数全局验证监听容器
     */
    private static final List<AbsCustomVerifyListener> CUSTOM_VERIFY_LISTENER_LIST = new ArrayList<>(16);

    public static List<AbsCustomVerifyListener> getCustomListenerList() {
        return CUSTOM_VERIFY_LISTENER_LIST;
    }

    /***
     * 默认构造函数，干了一件事情，就是会把所有实现了这个抽象类的子类实例全部添加到全局自定义验证监听器列表中
     * 因为在调用子类的构造函数时，会首先调用父类的构造器
     */
    public AbsCustomVerifyListener() {
        addCustomListener(this);
    }

    /**
     * 添加自定义监听器
     *
     * @param verifyListener 验证监听器
     */
    public synchronized static void addCustomListener(AbsCustomVerifyListener verifyListener) {
        CUSTOM_VERIFY_LISTENER_LIST.add(verifyListener);
    }

    /**
     * 业务系统自定义证书认证方法
     *
     * @param licenseExtra 自定义验证参数
     * @return boolean 是否成功
     */
    public abstract boolean verify(LicenseExtraParam licenseExtra) throws ApiException;
}
