package com.github.icezerocat.component.license.verify.interceptor;

import com.github.icezerocat.component.license.core.model.LicenseExtraParam;
import com.github.icezerocat.component.license.core.model.LicenseResult;
import com.github.icezerocat.component.license.core.model.LicenseVerifyManager;
import com.github.icezerocat.component.license.verify.annotion.VLicense;
import com.github.icezerocat.component.license.verify.config.LicenseVerifyProperties;
import com.github.icezerocat.component.license.verify.listener.AbsCustomVerifyListener;
import de.schlichtherle.license.LicenseContent;
import github.com.icezerocat.component.core.exception.ApiException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * Description: License验证拦截器
 * CreateDate:  2021/8/31 23:29
 *
 * @author zero
 * @version 1.0
 */
public class LicenseVerifyInterceptor implements HandlerInterceptor {
    @Resource
    private LicenseVerifyProperties licenseVerifyProperties;

    public LicenseVerifyInterceptor() {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            VLicense vLicense = method.getAnnotation(VLicense.class);

            //全局请求拦截或者注解拦截
            if (!Objects.isNull(vLicense) || this.licenseVerifyProperties.isGlobalLicense()) {
                LicenseVerifyManager licenseVerifyManager = new LicenseVerifyManager();
                /* 1、校验证书是否有效 */
                LicenseResult verifyResult = licenseVerifyManager.verify(licenseVerifyProperties.getVerifyParam());
                if (!verifyResult.getResult()) {
                    throw new ApiException(verifyResult.getMessage());
                }
                LicenseContent content = verifyResult.getContent();
                LicenseExtraParam licenseCheck = (LicenseExtraParam) content.getExtra();
                if (verifyResult.getResult()) {
                    /* 增加业务系统监听，是否自定义验证 */
                    List<AbsCustomVerifyListener> customListenerList = AbsCustomVerifyListener.getCustomListenerList();
                    boolean compare = true;
                    for (AbsCustomVerifyListener listener : customListenerList) {
                        boolean verify = listener.verify(licenseCheck);
                        compare = compare && verify;
                    }
                    return compare;
                }
                throw new ApiException(verifyResult.getException().getMessage());
            }
        }
        return true;
    }
}
