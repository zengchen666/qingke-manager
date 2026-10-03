package com.qk.mapper;

import com.qk.dto.UserQueryDto;
import com.qk.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户管理 Mapper
 */
@Mapper
public interface UserMapper {

    /**
     * 用户列表查询（左连接部门表、角色表，动态条件，见 UserMapper.xml）
     */
    List<User> list(UserQueryDto userQueryDto);

    /**
     * 新增用户
     */
    @Insert("insert into user(username, password, name, phone, email, gender, status, dept_id, role_id, image, remark, create_time, update_time) " +
            "values (#{username}, #{password}, #{name}, #{phone}, #{email}, #{gender}, #{status}, #{deptId}, #{roleId}, #{image}, #{remark}, #{createTime}, #{updateTime})")
    void insert(User user);

    /**
     * 批量删除用户
     */
    void deleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 根据角色标识查询用户列表（用于「分配线索」下拉框展示线索专员）
     */
    List<User> getUsersByRoleLabel(String roleLabel);

    /**
     * 根据用户名查询用户（登录用，同时带出角色标识）
     */
    @Select("select u.*, r.label role_label from user u left join role r on u.role_id = r.id where u.username = #{username}")
    User selectByUsername(String username);
}
