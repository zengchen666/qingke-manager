package com.qk.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * 用户ID，主键
     */
    private Integer id;

    /**
     * 用户名，唯一
     */
    private String username;

    /**
     * 密码，MD5 加密存储
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号，唯一
     */
    private String phone;

    /**
     * 邮箱，唯一
     */
    private String email;

    /**
     * 性别，1: 男，2: 女
     */
    private Integer gender;

    /**
     * 状态，1: 正常，0: 停用
     */
    private Integer status;

    /**
     * 部门ID，关联部门表主键
     */
    private Integer deptId;

    /**
     * 角色ID，关联角色表主键
     */
    private Integer roleId;

    /**
     * 头像URL
     */
    private String image;

    /**
     * 备注，50字以内
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    // ===== 扩展字段（数据库表中不存在，需标注 exist = false）=====

    /**
     * 部门名称 - 列表查询时左连接 dept 表带出
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 角色名称 - 列表查询时左连接 role 表带出
     */
    @TableField(exist = false)
    private String roleName;

    /**
     * 角色标识 - 登录时左连接 role 表带出，返回给前端
     */
    @TableField(exist = false)
    private String roleLabel;
}
