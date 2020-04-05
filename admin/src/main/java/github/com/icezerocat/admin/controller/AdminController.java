package github.com.icezerocat.admin.controller;

import github.com.icezerocat.admin.entity.OzapSalesErpInfo;
import github.com.icezerocat.admin.repository.SalesErpInfoRep;
import github.com.icezerocat.core.utils.DaoUtil;
import github.com.icezerocat.jdbctemplate.service.BaseJdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class AdminController {


    private final BaseJdbcTemplate baseJdbcTemplate;
    private final SalesErpInfoRep salesErpInfoRep;
    private final JdbcTemplate jdbcTemplate;

    public AdminController(SalesErpInfoRep salesErpInfoRep, JdbcTemplate jdbcTemplate, BaseJdbcTemplate baseJdbcTemplate) {
        this.salesErpInfoRep = salesErpInfoRep;
        this.jdbcTemplate = jdbcTemplate;
        this.baseJdbcTemplate = baseJdbcTemplate;
    }

    @RequestMapping("say")
    public String say() {
        // （当前页， 每页记录数， 排序方式）
        Pageable pageable = PageRequest.of(1, 10);
        Class<OzapSalesErpInfo> ozapSalesErpInfoClass = OzapSalesErpInfo.class;
        log.debug("{}", this.baseJdbcTemplate.findAll("select * from " + DaoUtil.getTableName(ozapSalesErpInfoClass),
                new Object[]{}, ozapSalesErpInfoClass, pageable));
        return "hello:" + new Date().toString();
    }
}
