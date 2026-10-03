package com.qk.interceptor;

import com.qk.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 令牌校验拦截器
 *
 * <p>与 {@link com.qk.filter.TokenFilter} 功能等价，是登录校验的另一种实现（Spring MVC 层面）。
 * 当前项目默认启用 Filter 方案，本拦截器保留实现，在 WebConfig 中注册即可切换。
 *
 * <p>Filter 与 Interceptor 的区别：
 * 接口规范不同（Filter 实现 jakarta.servlet.Filter，Interceptor 实现 HandlerInterceptor）；
 * 拦截范围不同（Filter 拦截所有资源，Interceptor 只拦截 Spring 环境中的资源）。
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    /**
     * 在请求处理之前进行令牌校验
     *
     * @param request  当前请求
     * @param response 当前响应
     * @param handler  被调用的处理器
     * @return true表示放行，false表示拦截
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求路径 URI /login
        String requestURI = request.getRequestURI();

        // 2.判断是否是登录请求
        if ("/login".equals(requestURI)) {
            log.info("当前是登录接口,直接放行");
            return true;
        }

        // 3.获取请求头(token)
        String token = request.getHeader("token");

        // 4.判断请求头是否是空 空-->响应401状态码
        if (token == null || token.isEmpty()) {
            log.info("token为空,请登录");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 5.解析token  失败 ->响应401状态码
        try {
            JwtUtils.parseToken(token);
        } catch (Exception e) {
            log.error("token解析失败");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 6.解析成功-->放行
        log.info("token校验成功,放行");
        return true;
    }
}
