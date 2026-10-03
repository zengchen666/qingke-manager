package com.qk.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录结果数据传输对象 view object
 * vo 包里面的对象，用于封装返回给前端的结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResultVo {
    private Integer id;
    private String username;
    private String name;
    private String image;
    private String roleLabel;
    private String token;
}
