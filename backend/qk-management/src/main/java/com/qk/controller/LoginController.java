package com.qk.controller;

import com.qk.common.Result;
import com.qk.entity.User;
import com.qk.service.UserService;
import com.qk.vo.LoginResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器
 *
 * <p>登录失败（用户名或密码错误、账号停用）由 UserService 抛出 BusinessException，
 * 经 GlobalExceptionHandler 统一转成 {code:0, msg:...} 响应。
 */
@Slf4j
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     *
     * @param user 接收前端传入的 username、password
     * @return 登录成功返回用户信息 + token
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        log.info("用户登录：{}", user.getUsername());
        LoginResultVo loginResultVo = userService.login(user);
        return Result.success(loginResultVo);
    }
}
