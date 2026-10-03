package com.qk.config;

import com.qk.filter.TokenFilter;
import com.qk.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置类（注册过滤器、拦截器）
 *
 * <p><b>过滤器注册说明（与文档的差异）：</b>
 * Spring Boot 4 移除了 {@code @ServletComponentScan}，@WebFilter 注解不再自动生效，
 * 这里改用 FilterRegistrationBean 显式注册 TokenFilter，等价且顺序可控。
 *
 * <p><b>为什么拦截器默认不注册：</b>
 * 令牌校验已在 {@link TokenFilter} 中完成，且 Filter 还负责把当前登录用户 ID
 * 写入 ThreadLocal（见 {@link com.qk.utils.CurrentUserHolder}）。同时开启拦截器会导致
 * 同一次请求重复解析两次 JWT。想改用拦截器方案时，取消 addInterceptors 中的注释即可（二选一）。
 */
@Configuration // 标识当前是一个配置类
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Autowired
    private Environment environment;

    /**
     * 显式设置服务端口（默认 8080）
     *
     * <p><b>为什么需要这段代码（本机实测结论）：</b>
     * Spring Boot 4.0.8 下，Environment 中名为 {@code configurationProperties} 的属性源
     * 会给出一个"自动分配"的端口（本机为 60175），且它的优先级高于 application.yml ——
     * 实测把 yml 配成 8080 或 8081 都不生效，Tomcat 始终起在 60175，还容易与本机其他程序冲突。
     *
     * <p>因此这里直接对 WebServerFactory 设端口，并跳过那个视图源去读真正生效的配置：
     * 命令行 {@code --server.port=9090} 优先级最高，其次是 application.yml，最后是默认值 8080。
     */
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE) // 最后执行，避免被 Boot 自带的 customizer 覆盖
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> portCustomizer() {
        return factory -> factory.setPort(resolvePort());
    }

    /**
     * 解析端口：按属性源顺序取 server.port，但跳过 configurationProperties 这个视图源
     */
    private int resolvePort() {
        if (environment instanceof ConfigurableEnvironment ce) {
            for (PropertySource<?> ps : ce.getPropertySources()) {
                if ("configurationProperties".equals(ps.getName())) {
                    continue; // 该源会给出自动分配的端口，不是用户配置，跳过
                }
                Object value = ps.getProperty("server.port");
                if (value != null) {
                    return Integer.parseInt(value.toString());
                }
            }
        }
        return 8080;
    }

    /**
     * 注册登录校验过滤器，拦截所有请求
     */
    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFilterRegistration() {
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("tokenFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册令牌校验拦截器 —— 与 TokenFilter 二选一，默认走 Filter 方案
        // registry.addInterceptor(tokenInterceptor).addPathPatterns("/**");
    }
}
