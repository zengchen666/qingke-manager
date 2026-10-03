package com.qk.service;

import com.qk.vo.OverviewVO;

/**
 * 报表统计 Service 接口
 */
public interface ReportService {

    /**
     * 获取首页概览数据（线索概览 + 商机概览）
     */
    OverviewVO getOverview();
}
