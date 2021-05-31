package github.com.icezerocat.component.mp.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.com.icezerocat.component.common.http.HttpResult;
import github.com.icezerocat.component.common.http.HttpStatus;
import github.com.icezerocat.component.mp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.component.mp.config.MpApplicationContextHelper;
import github.com.icezerocat.component.mp.model.MpModel;
import github.com.icezerocat.component.mp.model.MpResult;
import github.com.icezerocat.component.mp.service.BaseMpBuildService;
import github.com.icezerocat.component.mp.service.MpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final MpService mpService;

    public MpController(BaseMpBuildService baseMpBuildService, MpService mpService) {
        this.baseMpBuildService = baseMpBuildService;
        this.mpService = mpService;
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

    /**
     * mpCrud数据操作
     *
     * @param mpModel mo模型
     * @return 处理结果
     */
    @PostMapping("operation")
    public HttpResult operation(@RequestBody MpModel mpModel) {
        try {
            MpResult<Object> objectMpResult = this.mpService.invoke(mpModel);
            Object o = objectMpResult.getData();
            if (o instanceof HttpResult) {
                @SuppressWarnings("all")
                HttpResult<List<?>> oh = (HttpResult<List<?>>) o;
                return oh;
            }
            if (o instanceof Boolean) {
                boolean isDelete = (boolean) o;
                return isDelete ? HttpResult.ok("delete succeed") : HttpResult.error("delete failed");
            }
            if (o instanceof List) {
                @SuppressWarnings("all")
                List<Object> objectList = (List<Object>) o;
                return !CollectionUtils.isEmpty(objectList) ? HttpResult.ok(objectList) : HttpResult.error();
            }
            return HttpResult.ok(o);
        } catch (Exception e) {
            return HttpResult.error(e.getMessage());
        }

    }
}
