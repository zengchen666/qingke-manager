package com.qk.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门实体类
 */
@Data
public class Dept {
    private Integer id; // 部门id，主键
    private String name; // 部门名称
    private Integer status; // 状态：0-停用，1-正常
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 修改时间
}
