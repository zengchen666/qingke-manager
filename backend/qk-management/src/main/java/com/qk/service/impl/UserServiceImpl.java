package com.qk.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qk.common.PageResult;
import com.qk.dto.UserQueryDto;
import com.qk.entity.User;
import com.qk.exception.BusinessException;
import com.qk.mapper.UserMapper;
import com.qk.service.UserService;
import com.qk.utils.JwtUtils;
import com.qk.vo.LoginResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理 Service 实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    /**
     * 新增用户时的默认密码后缀
     */
    private static final String DEFAULT_PASSWORD_SUFFIX = "123";

    @Autowired
    private UserMapper userMapper;

    /**
     * 条件分页查询用户列表
     */
    @Override
    public PageResult<User> list(UserQueryDto userQueryDto) {
        PageHelper.startPage(userQueryDto.getPage(), userQueryDto.getPageSize());

        List<User> userList = userMapper.list(userQueryDto);

        Page<User> pageInfo = (Page<User>) userList;

        return new PageResult<User>(pageInfo.getTotal(), pageInfo.getResult());
    }

    /**
     * 新增用户
     */
    @Override
    public void add(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 默认密码 = 用户名 + "123"，MD5 加密后入库（MD5 只能加密不能解密）
        String password = user.getUsername() + DEFAULT_PASSWORD_SUFFIX;
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)));

        userMapper.insert(user);
    }

    /**
     * 根据 id 列表批量删除用户
     */
    @Override
    public void deleteByIds(List<Integer> ids) {
        userMapper.deleteByIds(ids);
    }

    /**
     * 根据角色标识查询用户列表
     */
    @Override
    public List<User> getUsersByRoleLabel(String roleLabel) {
        return userMapper.getUsersByRoleLabel(roleLabel);
    }

    /**
     * 登录
     */
    @Override
    public LoginResultVo login(User user) {
        // 1.根据前端传递的用户名,去数据库里面查询用户
        User u = userMapper.selectByUsername(user.getUsername());

        // 2.判断用户存不存在
        if (u == null) {
            log.info("用户不存在, 登录失败");
            throw new BusinessException("用户名或密码错误");
        }

        // 3.判断用户输入的密码是否正确（前端传明文，此处加密后与库中的密文比对）
        String userPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!userPassword.equals(u.getPassword())) {
            log.info("用户输入密码错误, 登录失败");
            throw new BusinessException("用户名或密码错误");
        }

        // 4.判断用户状态是不是可用
        if (u.getStatus() == null || u.getStatus() != 1) {
            log.info("用户已停用, 登录失败");
            throw new BusinessException("用户已停用, 登录失败");
        }

        // 5.封装返回结果对象
        LoginResultVo loginResultVo = new LoginResultVo();
        loginResultVo.setId(u.getId());
        loginResultVo.setUsername(u.getUsername());
        loginResultVo.setName(u.getName());
        loginResultVo.setImage(u.getImage());
        loginResultVo.setRoleLabel(u.getRoleLabel());

        // 6.生成 JWT 令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", u.getId());
        claims.put("username", u.getUsername());
        loginResultVo.setToken(JwtUtils.generateToken(claims));

        return loginResultVo;
    }
}
