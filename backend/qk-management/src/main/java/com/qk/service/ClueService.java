package com.qk.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qk.common.PageResult;
import com.qk.dto.ClueQueryDto;
import com.qk.entity.Clue;

/**
 * 线索管理 Service 接口
 * 继承 IService 后即拥有单表 CRUD 能力
 */
public interface ClueService extends IService<Clue> {

    /**
     * 线索列表（条件 + 分页）
     */
    PageResult<Clue> listClues(ClueQueryDto clueQueryDto);

    /**
     * 根据ID查询线索详细信息（含跟进记录列表）
     */
    Clue getClueById(Integer id);

    /**
     * 跟进线索：更新线索基本信息 + 添加一条跟进记录（事务）
     */
    void trackClue(Clue clue);

    /**
     * 将线索转为商机（事务）
     */
    void convertToBusiness(Integer id);
}
