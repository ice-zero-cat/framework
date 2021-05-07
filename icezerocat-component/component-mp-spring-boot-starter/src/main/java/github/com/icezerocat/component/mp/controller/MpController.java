package github.com.icezerocat.component.mp.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.common.http.HttpStatus;
import github.com.icezerocat.component.mp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.component.mp.config.MpApplicationContextHelper;
import github.com.icezerocat.component.mp.service.BaseMpBuildService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: mp控制器
 * CreateDate:  2020/11/4 9:55
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("mp")
public class MpController {

    private final BaseMpBuildService baseMpBuildService;

    public MpController(BaseMpBuildService baseMpBuildService) {
        this.baseMpBuildService = baseMpBuildService;
    }

    /**
     * 获取mapper
     *
     * @param tableName 表单名
     * @return baseMapper
     */
    @GetMapping("getMapper")
    public HttpResult getMapper(@RequestParam(defaultValue = "worker_node", required = false) String tableName) {
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(tableName);
        return HttpResult.Build.getInstance()
                .setCode(HttpStatus.SC_OK)
                .setCount(baseMapperObjectNoahService.count())
                .setData(MpApplicationContextHelper.getBeanNameByClass(baseMapperObjectNoahService.getClass()))
                .setMsg("data:表单对应的service。 count：表单数据总数")
                .complete();
    }
}
