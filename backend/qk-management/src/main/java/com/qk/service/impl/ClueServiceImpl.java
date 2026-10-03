package com.qk.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qk.common.PageResult;
import com.qk.dto.ClueQueryDto;
import com.qk.entity.Business;
import com.qk.entity.Clue;
import com.qk.entity.ClueTrackRecord;
import com.qk.mapper.BusinessMapper;
import com.qk.mapper.ClueMapper;
import com.qk.mapper.ClueTrackRecordMapper;
import com.qk.service.ClueService;
import com.qk.utils.CurrentUserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 线索管理 Service 实现类
 */
@Slf4j
@Service
public class ClueServiceImpl extends ServiceImpl<ClueMapper, Clue> implements ClueService {

    @Autowired
    private ClueTrackRecordMapper clueTrackRecordMapper;

    @Autowired
    private BusinessMapper businessMapper;

    /**
     * 线索列表（条件 + 分页，使用 MyBatis-Plus 分页插件）
     */
    @Override
    public PageResult<Clue> listClues(ClueQueryDto clueQueryDto) {
        IPage<Clue> page = this.baseMapper.listClues(
                new Page<>(clueQueryDto.getPage(), clueQueryDto.getPageSize()), clueQueryDto);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }

    /**
     * 根据ID查询线索详细信息
     */
    @Override
    public Clue getClueById(Integer id) {
        return this.baseMapper.getClueById(id);
    }

    /**
     * 跟进线索
     *
     * <p>涉及两次数据库操作（update 线索 + insert 跟进记录），必须加事务，
     * 否则第一步成功第二步失败会造成数据不一致。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void trackClue(Clue clue) {
        //1. 更新线索状态为“跟进中”
        clue.setStatus(3); // 状态值 3 表示跟进中
        clue.setUpdateTime(LocalDateTime.now());
        this.updateById(clue);

        //2. 添加跟进记录
        ClueTrackRecord trackRecord = new ClueTrackRecord();
        trackRecord.setClueId(clue.getId());
        trackRecord.setUserId(CurrentUserHolder.getCurrentUser()); // 当前登录用户ID（由 TokenFilter 写入 ThreadLocal）
        trackRecord.setSubject(clue.getSubject());
        trackRecord.setLevel(clue.getLevel());
        trackRecord.setRecord(clue.getRecord());
        trackRecord.setNextTime(clue.getNextTime());
        trackRecord.setType(1); // 类型 1 表示正常跟进
        trackRecord.setCreateTime(LocalDateTime.now());
        clueTrackRecordMapper.insert(trackRecord);
    }

    /**
     * 将线索转为商机
     *
     * <p>两步：① 线索状态置为 5（转为商机）；② 依据线索信息生成一条商机记录。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void convertToBusiness(Integer id) {
        // 1. 修改线索信息
        Clue clue = this.getById(id);
        if (clue == null) {
            throw new RuntimeException("线索不存在, id=" + id);
        }
        clue.setStatus(5); // 转为商机
        clue.setUpdateTime(LocalDateTime.now());
        this.updateById(clue);

        // 2. 创建商机信息
        Business business = new Business();
        BeanUtils.copyProperties(clue, business);
        business.setId(null);       // 商机ID为空, 表示新增, 主键自增
        business.setUserId(null);   // 商机归属人待分配
        business.setNextTime(null); // 下次联系时间置空
        business.setStatus(1);      // 1: 待分配
        business.setClueId(clue.getId());
        business.setCreateTime(LocalDateTime.now());
        business.setUpdateTime(LocalDateTime.now());

        // 3. 插入商机记录
        businessMapper.insert(business);
    }
}
