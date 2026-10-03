package com.qk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qk.entity.Business;
import com.qk.vo.OverviewVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商机管理 Mapper
 */
@Mapper
public interface BusinessMapper extends BaseMapper<Business> {

    /**
     * 获取商机概览数据
     */
    OverviewVO getBusinessOverviewData();
}
