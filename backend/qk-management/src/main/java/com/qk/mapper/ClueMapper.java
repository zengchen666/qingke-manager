package com.qk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qk.dto.ClueQueryDto;
import com.qk.entity.Clue;
import com.qk.vo.OverviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 线索管理 Mapper
 * 继承 BaseMapper 后即拥有单表 CRUD 能力，复杂 SQL（联表、统计）写在 ClueMapper.xml
 */
@Mapper
public interface ClueMapper extends BaseMapper<Clue> {

    /**
     * 线索列表（条件 + 分页，分页由 MyBatis-Plus 分页插件完成）
     */
    IPage<Clue> listClues(IPage<Clue> cluePage, @Param("clueQueryDto") ClueQueryDto clueQueryDto);

    /**
     * 根据ID查询线索详细信息（含跟进记录列表）
     */
    Clue getClueById(Integer id);

    /**
     * 获取线索总览数据
     */
    OverviewVO getClueOverviewData();
}
