package com.qk.service;

import com.qk.common.PageResult;
import com.qk.entity.Dept;

/**
 * 部门管理 Service 接口
 */
public interface DeptService {

    /**
     * 新增部门
     */
    void addDept(Dept dept);

    /**
     * 条件分页查询部门列表
     */
    PageResult<Dept> list(String name, Integer status, Integer page, Integer pageSize);

    /**
     * 根据id查询部门
     */
    Dept getById(Integer id);

    /**
     * 修改部门
     */
    void updateDept(Dept dept);

    /**
     * 根据id删除部门
     */
    void deleteById(Integer id);
}
