package com.qk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 轻客管家 - 后台管理模块启动类
 *
 * <p><b>与文档的差异：</b>
 * 文档使用 {@code @ServletComponentScan} + {@code @WebFilter} 注册过滤器。
 * Spring Boot 4.0 已移除 {@code org.springframework.boot.web.servlet.ServletComponentScan}，
 * 改为在 {@link com.qk.config.WebConfig} 中用 FilterRegistrationBean 显式注册 TokenFilter，
 * 效果等价且顺序可控。
 *
 * <p><b>端口说明：</b>
 * 本机实测 Spring Boot 4.0.8 下 application.yml 中的 {@code server.port} 未被绑定到
 * ServerProperties（配 8080 / 8081 均无效，会退化成自动分配的端口，且易与本机其他程序冲突）。
 * 因此在 {@link com.qk.config.WebConfig#portCustomizer(org.springframework.core.env.Environment)}
 * 中直接设置端口，默认 8080，同时仍尊重命令行 {@code --server.port=xxxx}。
 */
@SpringBootApplication
public class QkManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(QkManagementApplication.class, args);
    }

}
