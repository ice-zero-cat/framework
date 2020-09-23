package github.com.icezerocat.mybatismp.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.com.icezerocat.core.http.HttpResult;
import github.com.icezerocat.mybatismp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.mybatismp.model.Search;
import github.com.icezerocat.mybatismp.service.BaseCurdService;
import github.com.icezerocat.mybatismp.service.BaseMpBuildService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Description: 增删查改控制器
 * CreateDate:  2020/8/7 9:25
 *
 * @author zero
 * @version 1.0
 */
@Slf4j
@RestController
@Api(tags = "增删查改控制器")
@RequestMapping("curd")
public class CurdController {
    @Resource
    private BaseCurdService baseCurdService;
    @Resource
    private BaseMpBuildService baseMpBuildService;

    /**
     * 查询
     *
     * @param beanName bean名
     * @param page     页码
     * @param limit    每一页大小
     * @param searches 搜索条件
     * @return 查询结果
     */
    @ApiOperation("查询")
    @PostMapping("retrieve")
    public HttpResult retrieve(@RequestParam String beanName,
                               @RequestParam(required = false, defaultValue = "-1") long page,
                               @RequestParam(required = false, defaultValue = "-1") long limit,
                               @RequestBody(required = false) List<Search> searches) {
        return this.baseCurdService.retrieve(beanName, page, limit, searches);
    }

    /**
     * 删除
     *
     * @param beanName bean名
     * @param ids      id集合
     * @return 删除结果
     */
    @ApiOperation("删除")
    @DeleteMapping("delete")
    public HttpResult delete(@RequestParam String beanName, @RequestParam List<Long> ids) {
        return this.baseCurdService.delete(beanName, ids);
    }

    /**
     * 插入数据
     *
     * @param beanName   实现类名
     * @param entityName 对象名
     * @param mapList    插入数据
     * @return 插入结果
     */
    @ApiOperation("插入数据")
    @PostMapping("create")
    public HttpResult create(@RequestParam String beanName, @RequestParam String entityName, @RequestBody List<Map<String, Object>> mapList) {
        return HttpResult.ok(this.baseCurdService.saveOrUpdateBatch(beanName, entityName, mapList));
    }

    /**
     * 插入数据
     *
     * @param tableName  实现类名
     * @param objectList 对象名
     * @return 插入结果
     */
    @ApiOperation("插入数据")
    @PostMapping("createByTable")
    public HttpResult createByTable(@RequestParam String tableName, @RequestBody List<Object> objectList) {
        log.debug("父类对象接收结果：{}", objectList);
        return HttpResult.ok(this.baseCurdService.saveOrUpdateBatch(tableName, objectList));
    }

    /**
     * 注册mapper
     *
     * @param tableName 表单名
     * @return 操作结果
     */
    @ApiOperation("注册mapper")
    @PostMapping("registeredMapper")
    public HttpResult registeredMapper(@RequestParam String tableName) {
        NoahServiceImpl<BaseMapper<Object>, Object> baseMapperObjectNoahService = this.baseMpBuildService.newInstance(tableName);
        if (baseMapperObjectNoahService != null) {
            return HttpResult.ok();
        }
        return HttpResult.error();
    }
}
