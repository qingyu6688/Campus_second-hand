package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名/学号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 学生证/校园卡照片URL
     */
    private String studentIdImg;

    /**
     * 认证状态 (0:未认证, 1:审核中, 2:已认证, 3:驳回)
     */
    private Integer isVerified;

    /**
     * 宿舍楼
     */
    private String dormitory;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 账号状态 (1:正常, 0:禁用)
     */
    private Integer status;

    /**
     * 逻辑删除 (0:未删除, 1:已删除)
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
