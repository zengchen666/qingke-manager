package com.qk.filter;

import com.qk.utils.CurrentUserHolder;
import com.qk.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 登录校验过滤器（当前启用的令牌校验方案）
 *
 * <p>职责：
 * <ol>
 *   <li>放行登录接口；</li>
 *   <li>校验请求头 token，缺失或解析失败 → 响应 401；</li>
 *   <li>解析成功后把登录用户 ID 写入 ThreadLocal，供 Service 层获取当前登录人；</li>
 *   <li>请求结束后必须 remove，避免线程复用导致用户信息串号。</li>
 * </ol>
 *
 * <p>注册方式见 {@link com.qk.config.WebConfig#tokenFilterRegistration()}：
 * Spring Boot 4 移除了 @ServletComponentScan，@WebFilter 不会再被自动扫描，
 * 因此这里不再标注 @WebFilter，改由 FilterRegistrationBean 显式注册。
 */
@Slf4j
public class TokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 放行登录接口
        if ("/login".equals(request.getRequestURI())) {
            log.info("登录操作, 直接放行");
            chain.doFilter(request, response);
            return;
        }

        // 提取请求头中的 token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            log.info("请求头中没有 token, 拒绝访问, 401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // 解析 token
            Claims claims = JwtUtils.parseToken(token);
            // 获取用户id, 存入ThreadLocal
            Integer userId = claims.get("id", Integer.class);
            CurrentUserHolder.setCurrentUser(userId);
        } catch (Exception e) {
            // 捕获解析 token 异常，返回 401 错误
            log.info("解析 token 出错, 拒绝访问, 401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // 继续执行后续过滤器或请求处理
            log.info("token 正确, 继续执行后续过滤器或请求处理");
            chain.doFilter(request, response);
        } finally {
            // 请求结束后清理 ThreadLocal（放在 finally 中，保证异常情况下也能清理）
            CurrentUserHolder.removeCurrentUser();
        }
    }

}
