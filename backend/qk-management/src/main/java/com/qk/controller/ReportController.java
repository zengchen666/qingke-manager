package com.qk.controller;

import com.qk.common.Result;
import com.qk.service.ReportService;
import com.qk.vo.OverviewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报表统计控制器（首页概览）
 */
@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 获取首页概览数据 - /report/overview
     */
    @GetMapping("/overview")
    public Result getOverview() {
        log.info("获取首页概览数据");
        OverviewVO overview = reportService.getOverview();
        return Result.success(overview);
    }

}
