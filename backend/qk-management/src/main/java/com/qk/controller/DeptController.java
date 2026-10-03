package com.qk.controller;

import com.qk.common.PageResult;
import com.qk.common.Result;
import com.qk.entity.Dept;
import com.qk.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 部门管理控制器
 *
 * <p>RESTful 风格：/depts 表示部门这类资源的集合
 */
@Slf4j
@RequestMapping("/depts")  // 公共路径抽取：完整路径 = 类上的 value + 方法上的 value
@RestController
public class DeptController {

    @Autowired
    private DeptService deptService;

    /**
     * 新增部门
     */
    @PostMapping
    public Result addDept(@RequestBody Dept dept) {
        log.info("新增部门: {}", dept);
        deptService.addDept(dept);
        return Result.success();
    }

    /**
     * 条件分页查询部门列表
     * 请求示例：/depts?name=研发&status=1&page=1&pageSize=10
     */
    @GetMapping
    public Result getDepts(String name, Integer status,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询部门列表: name={}, status={}, page={}, pageSize={}", name, status, page, pageSize);
        PageResult<Dept> pageResult = deptService.list(name, status, page, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询部门（用于修改时的数据回显）
     * 路径参数：通过请求URL直接传递参数，使用 {...} 标识，用 @PathVariable 获取
     */
    @GetMapping("/{id}")
    public Result getDeptById(@PathVariable Integer id) {
        log.info("根据id查询部门: {}", id);
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }

    /**
     * 修改部门信息
     */
    @PutMapping
    public Result updateDept(@RequestBody Dept dept) {
        log.info("修改部门信息: {}", dept);
        deptService.updateDept(dept);
        return Result.success();
    }

    /**
     * 根据id删除部门
     */
    @DeleteMapping("/{id}")
    public Result deleteDeptById(@PathVariable Integer id) {
        log.info("删除部门: {}", id);
        deptService.deleteById(id);
        return Result.success();
    }
}
