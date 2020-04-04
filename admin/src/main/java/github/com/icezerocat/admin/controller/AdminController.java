package github.com.icezerocat.admin.controller;

import github.com.icezerocat.admin.entity.OzapSalesErpInfo;
import github.com.icezerocat.admin.jdbctemplate.BaseJdbcTemplate;
import github.com.icezerocat.admin.repository.SalesErpInfoRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Table;
import java.util.Arrays;
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
@RestController
public class AdminController {


    @Autowired
    private BaseJdbcTemplate baseJdbcTemplate;

    private final SalesErpInfoRep salesErpInfoRep;
    private final JdbcTemplate jdbcTemplate;

    public AdminController(SalesErpInfoRep salesErpInfoRep, JdbcTemplate jdbcTemplate) {
        this.salesErpInfoRep = salesErpInfoRep;
        this.jdbcTemplate = jdbcTemplate;
    }

    @RequestMapping("say")
    public String say() {
        List<Long> ids = Arrays.asList(91L, 92L, 107L);
        //log.debug("{}", this.salesErpInfoRep.findAllById(ids));
        this.salesErpInfoRep.findAll();
        String sql = "select * from " + OzapSalesErpInfo.class.getAnnotation(Table.class).name() + " where ID = 91";
        log.debug("{}", this.baseJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(OzapSalesErpInfo.class)));
        return "hello:" + new Date().toString();
    }
}
