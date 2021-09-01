package com.github.icezerocat.component.license.core.web.controller;

import com.github.icezerocat.component.license.core.service.AbsServerInfos;
import github.com.icezerocat.component.common.http.HttpResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 服务器硬件信息获取API
 * CreateDate:  2021/8/30 20:01
 *
 * @author zero
 * @version 1.0
 */
@RestController
@RequestMapping("/license")
public class HardWareInfoController {

    /**
     * <p>获取服务器硬件信息</p>
     *
     * @param osName 操作系统类型，如果为空则自动判断
     * @return 服务器硬件信息
     */
    @RequestMapping(value = "/getServerInfos")
    public HttpResult getServerInfos(@RequestParam(value = "osName", required = false) String osName) {
        return HttpResult.ok(AbsServerInfos.getServer(osName).getServerInfos());
    }
}
