package com.qk.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qk.common.PageResult;
import com.qk.entity.Dept;
import com.qk.mapper.DeptMapper;
import com.qk.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门管理 Service 实现类
 */
@Slf4j
@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    /**
     * 新增部门
     */
    @Override
    public void addDept(Dept dept) {
        //1. 补全基础属性
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        //2. 插入数据
        deptMapper.insertDept(dept);
    }

    /**
     * 条件分页查询（基于 PageHelper 分页插件）
     */
    @Override
    public PageResult<Dept> list(String name, Integer status, Integer page, Integer pageSize) {
        // 开始分页
        PageHelper.startPage(page, pageSize);
        // 执行查询（Mapper 中无需考虑任何分页逻辑）
        List<Dept> depts = deptMapper.list(name, status);
        // 获取分页信息
        Page<Dept> pageInfo = (Page<Dept>) depts;
        // 封装成 PageResult 对象
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getResult());
    }

    /**
     * 根据id查询部门（修改前的回显）
     */
    @Override
    public Dept getById(Integer id) {
        return deptMapper.getById(id);
    }

    /**
     * 修改部门
     */
    @Override
    public void updateDept(Dept dept) {
        //1. 补全基础属性
        dept.setUpdateTime(LocalDateTime.now());
        //2. 修改数据
        deptMapper.updateDept(dept);
    }

    /**
     * 根据id删除部门
     */
    @Override
    public void deleteById(Integer id) {
        deptMapper.deleteById(id);
    }
}
