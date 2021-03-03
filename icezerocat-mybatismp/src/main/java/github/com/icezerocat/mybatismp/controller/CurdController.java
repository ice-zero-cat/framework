package github.com.icezerocat.mybatismp.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import github.com.icezerocat.core.http.HttpResult;
import github.com.icezerocat.mybatismp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.mybatismp.model.Search;
import github.com.icezerocat.mybatismp.page.PageResult;
import github.com.icezerocat.mybatismp.page.SearchPageRequest;
import github.com.icezerocat.mybatismp.service.BaseCurdService;
import github.com.icezerocat.mybatismp.service.BaseMpBuildService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        return HttpResult.ok(this.baseCurdService.saveOrUpdateBatchByTableName(tableName, objectList));
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

    /**
     * 查询
     *
     * @param tableName bean名
     * @return 查询结果
     */
    @ApiOperation("查询")
    @PostMapping("retrieveByTable")
    public HttpResult retrieveByTable(@RequestParam String tableName,
                                      @RequestBody(required = false) SearchPageRequest searchPageRequest) {
        searchPageRequest = searchPageRequest != null ? searchPageRequest : new SearchPageRequest();
        HttpResult retrieve = this.baseCurdService.retrieve(
                tableName,
                searchPageRequest.getPageNum(),
                searchPageRequest.getPageSize(),
                searchPageRequest.getSearches()
        );
        PageResult pageResult;
        if (searchPageRequest.getPageNum() > -1 && searchPageRequest.getPageSize() > -1) {
            Page ipage = retrieve.getData() == null ? new Page() : (Page) retrieve.getData();
            pageResult = PageResult.getPageResult(ipage.getRecords(), ipage.getSize(), searchPageRequest);
        } else {
            List list = retrieve.getData() == null ? new ArrayList() : (List) retrieve.getData();
            pageResult = PageResult.getPageResult(list, list.size(), searchPageRequest);
        }
        return HttpResult.ok(pageResult);
    }

    /**
     * 根据已有对象进行查询
     *
     * @param entityName 对象名
     * @return 查询结果
     */
    @ApiOperation("根据已有对象进行查询")
    @PostMapping("retrieveByEntity")
    public HttpResult retrieveByEntity(@RequestParam String entityName,
                                       @RequestBody(required = false) SearchPageRequest searchPageRequest) {
        searchPageRequest = searchPageRequest != null ? searchPageRequest : new SearchPageRequest();
        HttpResult<List<?>> retrieve = this.baseCurdService.retrieveByEntity(
                entityName,
                searchPageRequest.getPageNum(),
                searchPageRequest.getPageSize(),
                searchPageRequest.getSearches());
        PageResult pageResult = PageResult.getPageResult(retrieve.getData(),
                retrieve.getCount(), searchPageRequest);
        return HttpResult.ok(pageResult);
    }

    /**
     * 删除
     *
     * @param tableName bean名
     * @param ids       id集合
     * @return 删除结果
     */
    @ApiOperation("删除")
    @DeleteMapping("deleteByTableName")
    public HttpResult deleteByTableName(@RequestParam String tableName, @RequestBody List<Long> ids) {
        return this.baseCurdService.deleteByTableName(tableName, ids);
    }

    /**
     * 根据搜索条件删除
     *
     * @param tableName bean名
     * @param searches  搜搜条件
     * @return 删除结果
     */
    @ApiOperation("根据搜索条件删除")
    @DeleteMapping("deleteBySearch")
    public HttpResult deleteBySearch(@RequestParam String tableName, @RequestBody List<Search> searches) {
        return this.baseCurdService.deleteBySearch(tableName, searches);
    }

    /**
     * 删除根据已有对象，搜索删除（自定义对象）
     *
     * @param entityName bean名
     * @param searches   搜索条件
     * @return 删除结果 int
     */
    @ApiOperation("删除根据已有对象，搜索删除（自定义对象）")
    @DeleteMapping("deleteByEntitySearch")
    public HttpResult deleteByEntitySearch(@RequestParam String entityName, @RequestBody List<Search> searches) {
        return this.baseCurdService.deleteByEntitySearch(entityName, searches);
    }

    /**
     * 批量保存或更新对象
     *
     * @param entityName 对象名称（自定义复杂的对象-非自动生成）
     * @param mapList    对象数据据（list）
     * @return 保存结果
     */
    @ApiOperation("批量保存或更新对象")
    @PostMapping("saveOrUpdateBatch")
    public HttpResult saveOrUpdateBatch(@RequestParam String entityName, @RequestBody List<Map<String, Object>> mapList) {
        return this.baseCurdService.saveOrUpdateBatch(entityName, mapList);
    }
}
