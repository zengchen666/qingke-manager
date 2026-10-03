package com.qk.controller;

import com.qk.common.PageResult;
import com.qk.common.Result;
import com.qk.dto.UserQueryDto;
import com.qk.entity.User;
import com.qk.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户管理控制器
 */
@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 条件分页查询用户列表
     */
    @GetMapping
    public Result list(UserQueryDto userQueryDto) {
        log.info("用户查询参数: {}", userQueryDto);
        PageResult<User> pageResult = userService.list(userQueryDto);
        return Result.success(pageResult);
    }

    /**
     * 根据角色标识查询用户列表（分配线索时，下拉展示对应的线索专员）
     */
    @GetMapping("/role/{roleLabel}")
    public Result getUsersByRoleLabel(@PathVariable("roleLabel") String roleLabel) {
        log.info("根据角色标识查询用户列表, roleLabel: {}", roleLabel);
        List<User> users = userService.getUsersByRoleLabel(roleLabel);
        return Result.success(users);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result addUser(@RequestBody User user) {
        log.info("新增用户: {}", user);
        userService.add(user);
        return Result.success();
    }

    /**
     * 根据id列表批量删除用户
     * 单个删除也走这个接口：/users/1
     */
    @DeleteMapping("/{ids}")
    public Result deleteUserByIds(@PathVariable List<Integer> ids) {
        log.info("删除用户, ids: {}", ids);
        userService.deleteByIds(ids);
        return Result.success();
    }
}
