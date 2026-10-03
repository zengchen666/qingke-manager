package com.qk.service;

import com.qk.common.PageResult;
import com.qk.dto.UserQueryDto;
import com.qk.entity.User;
import com.qk.vo.LoginResultVo;

import java.util.List;

/**
 * 用户管理 Service 接口
 */
public interface UserService {

    /**
     * 条件分页查询用户列表
     */
    PageResult<User> list(UserQueryDto userQueryDto);

    /**
     * 新增用户（密码使用默认规则：用户名 + "123"，MD5 加密后存储）
     */
    void add(User user);

    /**
     * 根据 id 列表批量删除用户
     */
    void deleteByIds(List<Integer> ids);

    /**
     * 根据角色标识查询用户列表
     */
    List<User> getUsersByRoleLabel(String roleLabel);

    /**
     * 登录：校验用户名、密码、状态，通过后返回用户信息 + JWT 令牌
     */
    LoginResultVo login(User user);
}
