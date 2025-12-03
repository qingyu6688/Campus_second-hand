package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 互动留言
 */
@Data
@TableName("biz_comment")
public class BizComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 留言用户ID
     */
    private Long userId;

    /**
     * 父留言ID (若为顶级留言则为NULL)
     */
    private Long parentId;

    /**
     * 被回复用户ID (若有)
     */
    private Long replyUserId;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 留言时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private com.campus.entity.SysUser user;

    @TableField(exist = false)
    private com.campus.entity.BizGoods goods;

    @TableField(exist = false)
    private java.util.List<BizComment> children;
}
