package com.qk.mapper;

import com.qk.entity.Dept;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门管理 Mapper
 * 简单 SQL 用注解，动态 SQL（条件查询、动态更新）写在 DeptMapper.xml 中
 */
@Mapper
public interface DeptMapper {

    /**
     * 插入部门信息
     */
    @Insert("INSERT INTO dept (name, status, create_time, update_time) VALUES (#{name}, #{status}, #{createTime}, #{updateTime})")
    void insertDept(Dept dept);

    /**
     * 条件查询部门列表（分页由 PageHelper 在 Service 层完成，此处无需关心分页）
     */
    List<Dept> list(@Param("name") String name, @Param("status") Integer status);

    /**
     * 根据id查询数据
     */
    @Select("select id, name, status, create_time, update_time from dept where id = #{id}")
    Dept getById(Integer id);

    /**
     * 修改部门信息（动态SQL，见 DeptMapper.xml）
     */
    void updateDept(Dept dept);

    /**
     * 根据id删除部门
     */
    @Delete("delete from dept where id = #{id}")
    void deleteById(Integer id);
}
