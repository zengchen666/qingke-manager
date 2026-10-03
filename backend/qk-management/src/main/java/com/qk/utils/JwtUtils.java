package com.qk.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 令牌工具类
 *
 * <p><b>与文档的差异（必须了解的兼容性改动）：</b>
 * <ol>
 *   <li>文档使用 jjwt 0.9.1，它依赖 javax.xml.bind 与 jackson-databind 2.x；
 *       Spring Boot 4 内置 Jackson 3（包名已由 com.fasterxml.jackson 改为 tools.jackson），
 *       jjwt 0.9.1 在该环境下会 NoClassDefFoundError，故升级为 jjwt 0.12.x。</li>
 *   <li>jjwt 0.12 对 HS256 有密钥强度校验（>= 256 bit，即 32 字节），
 *       文档中的 "cWluZ2tl" 仅 8 字节会抛 WeakKeyException，故换成足够长度的密钥。</li>
 *   <li>0.12 的 API 为 verifyWith(SecretKey) + parseSignedClaims，与 0.9.1 的
 *       setSigningKey + parseClaimsJws 写法不同，业务调用方代码不受影响。</li>
 * </ol>
 */
public class JwtUtils {

    /**
     * 秘钥，HS256 算法要求长度不小于 256 bit（32 字节）
     *
     * <p>优先从环境变量 JWT_SECRET 读取，未设置时回落到开发默认值。
     * 密钥硬编码在代码里等于把身份伪造的钥匙公开，所以正式环境必须用环境变量注入。
     */
    private static final String SECRET_KEY = envOrDefault(
            "JWT_SECRET", "qingke-guanjia-jwt-secret-key-cWluZ2tl-2026");

    private static String envOrDefault(String name, String defaultValue) {
        String v = System.getenv(name);
        if (v == null || v.isBlank()) {
            v = System.getProperty(name);
        }
        return (v == null || v.isBlank()) ? defaultValue : v;
    }

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * 令牌有效期（12小时）
     */
    private static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000;

    /**
     * 生成JWT令牌
     *
     * @param claims 自定义声明信息
     * @return 生成的JWT令牌
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()                                                   // 构建令牌
                .claims(claims)                                                 // 设置载荷中的数据 map格式
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                .signWith(KEY)                                                  // 签名秘钥和加密算法
                .compact();
    }

    /**
     * 解析JWT令牌
     *
     * @param token JWT令牌
     * @return 解析后的Claims对象
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()            // 解析令牌
                .verifyWith(KEY)        // 设置秘钥
                .build()
                .parseSignedClaims(token) // 解析token
                .getPayload();          // 获取载荷中的数据
    }

}
