package com.qk.dto;

import lombok.Data;

/**
 * 用户查询参数封装类
 * 文档 6.2 节中以 UserDto 命名，此处为语义更明确改为 UserQueryDto
 */
@Data
public class UserQueryDto {

    /**
     * 姓名（模糊匹配）
     */
    private String name;

    /**
     * 手机号（模糊匹配）
     */
    private String phone;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 状态，1: 正常，0: 停用
     */
    private Integer status;

    /**
     * 分页查询的页码，默认为1
     */
    private Integer page = 1;

    /**
     * 分页查询的每页记录数，默认为10
     */
    private Integer pageSize = 10;
}
