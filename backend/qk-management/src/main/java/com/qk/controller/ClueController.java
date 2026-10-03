package com.qk.controller;

import com.qk.common.PageResult;
import com.qk.common.Result;
import com.qk.dto.ClueQueryDto;
import com.qk.entity.Clue;
import com.qk.service.ClueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 线索管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/clues")
public class ClueController {

    @Autowired
    private ClueService clueService;

    /**
     * 新增线索
     */
    @PostMapping
    public Result addClue(@RequestBody Clue clue) {
        log.info("新增线索: {}", clue);
        clue.setStatus(1); // 1: 待分配
        clue.setCreateTime(LocalDateTime.now());
        clue.setUpdateTime(LocalDateTime.now());

        clueService.save(clue);
        return Result.success();
    }

    /**
     * 线索列表（条件 + 分页）
     */
    @GetMapping
    public Result listClues(ClueQueryDto clueQueryDto) {
        log.info("查询线索列表, 参数: {}", clueQueryDto);
        PageResult<Clue> pageResult = clueService.listClues(clueQueryDto);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询线索详细信息（用于跟进页面的数据回显）
     */
    @GetMapping("/{id}")
    public Result getClueById(@PathVariable Integer id) {
        log.info("根据ID查询线索详细信息, id: {}", id);
        Clue clue = clueService.getClueById(id);
        return Result.success(clue);
    }

    /**
     * 分配线索：管理员将「待分配」的线索指派给某个线索专员
     * 请求示例：PUT /clues/assign/1/2  （1=线索ID, 2=用户ID）
     */
    @PutMapping("/assign/{clueId}/{userId}")
    public Result assignClue(@PathVariable Integer clueId, @PathVariable Integer userId) {
        log.info("分配线索: 线索ID={}, 用户ID={}", clueId, userId);

        // 查询线索是否存在
        Clue clue = clueService.getById(clueId);
        if (clue == null) {
            return Result.error("线索不存在, id=" + clueId);
        }

        Clue update = new Clue();
        update.setId(clueId);
        update.setStatus(2); // 2: 待跟进
        update.setUserId(userId); // 设置归属人ID
        update.setUpdateTime(LocalDateTime.now());

        clueService.updateById(update);
        return Result.success();
    }

    /**
     * 跟进线索：更新线索基本信息 + 新增一条跟进记录
     */
    @PutMapping
    public Result trackClue(@RequestBody Clue clue) {
        log.info("跟进线索: {}", clue);
        clueService.trackClue(clue);
        return Result.success();
    }

    /**
     * 将线索转为商机
     */
    @PutMapping("/toBusiness/{id}")
    public Result convertToBusiness(@PathVariable Integer id) {
        log.info("将线索转为商机, id: {}", id);
        clueService.convertToBusiness(id);
        return Result.success();
    }
}
