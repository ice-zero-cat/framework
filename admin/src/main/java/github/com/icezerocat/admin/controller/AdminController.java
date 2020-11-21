package github.com.icezerocat.admin.controller;

import github.com.icezerocat.admin.entity.OzapSalesErpInfo;
import github.com.icezerocat.core.config.DruidConfig;
import github.com.icezerocat.core.service.DbService;
import github.com.icezerocat.core.utils.DaoUtil;
import github.com.icezerocat.jdbctemplate.service.BaseJdbcTemplate;
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
}
