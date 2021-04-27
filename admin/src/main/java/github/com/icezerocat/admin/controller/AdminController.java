package github.com.icezerocat.admin.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.icezerocat.component.common.http.HttpResult;
import com.github.icezerocat.component.db.config.DruidConfig;
import com.github.icezerocat.component.db.service.DbService;
import github.com.icezerocat.admin.entity.OzapSalesErpInfo;
import github.com.icezerocat.core.utils.DaoUtil;
import github.com.icezerocat.jdbctemplate.service.BaseJdbcTemplate;
import github.com.icezerocat.mybatismp.common.mybatisplus.NoahServiceImpl;
import github.com.icezerocat.mybatismp.service.BaseMpBuildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * ProjectName: [framework]
 * Package:     [github.com.icezerocat.admin.controller.AdminController]
 * Description: admin控制器
 * CreateDate:  2020/4/4 14:38
 *
 * @author 0.0.0
 * @version 1.0
 */
@Slf4j
@RestController("/")
@RequiredArgsConstructor
public class AdminController {


    private final BaseJdbcTemplate baseJdbcTemplate;
    private final DbService dbService;
    private final DruidConfig druidConfig;
    private final BaseMpBuildService baseMpBuildService;


    @RequestMapping("say")
    public String say() {
        // （当前页， 每页记录数， 排序方式）
        Pageable pageable = PageRequest.of(1, 10);
        Class<OzapSalesErpInfo> ozapSalesErpInfoClass = OzapSalesErpInfo.class;
        log.debug("{}", this.baseJdbcTemplate.findAll("select * from " + DaoUtil.getTableName(ozapSalesErpInfoClass),
                new Object[]{}, ozapSalesErpInfoClass, pageable));
        return "hello:" + new Date().toString();
    }

    /**
     * druid链接池配置
     *
     * @throws SQLException 数据池链接
     */
    @GetMapping("mp")
    public void mp() throws SQLException {
        Connection connectionByDruid = this.druidConfig.getConnectionByDruid();
        log.debug("{}", connectionByDruid);
        connectionByDruid.close();
    }

    /**
     * mp构建类处理缓存问题
     *
     * @return 总数
     */
    @GetMapping("mpBuild")
    public HttpResult mpBuild() {
        NoahServiceImpl<BaseMapper<Object>, Object> noahService = this.baseMpBuildService.newInstance("oz_ap_def_event");
        log.debug("数据总数：{}", noahService.count());
        List<Object> list = noahService.list();
        log.debug("{}", list);
        return HttpResult.ok(noahService.count());
    }

}
