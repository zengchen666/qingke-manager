package com.qk.service.impl;

import com.qk.mapper.BusinessMapper;
import com.qk.mapper.ClueMapper;
import com.qk.service.ReportService;
import com.qk.vo.OverviewVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 报表统计 Service 实现类
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ClueMapper clueMapper;

    @Autowired
    private BusinessMapper businessMapper;

    @Override
    public OverviewVO getOverview() {
        //1. 获取线索概览数据
        OverviewVO clueOverviewVO = clueMapper.getClueOverviewData();

        //2. 获取商机概览数据
        OverviewVO businessOverviewVO = businessMapper.getBusinessOverviewData();

        //3. 合并数据返回（把商机数据拷贝到线索VO中，忽略线索相关字段，保留线索统计值）
        BeanUtils.copyProperties(businessOverviewVO, clueOverviewVO,
                "clueTotal", "clueWaitAllot", "clueWaitFollow", "clueFollowing", "clueFalse", "clueConvertBusiness");
        return clueOverviewVO;
    }
}
