package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品
 */
@Data
@TableName("biz_goods")
public class BizGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 售价
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 交易地点
     */
    private String location;

    /**
     * 交易方式 (1:自提, 2:送货上门, 3:邮寄)
     */
    private Integer deliveryType;

    /**
     * 标签 (JSON数组)
     */
    private String tags;

    /**
     * 图片 (JSON数组)
     */
    private String images;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 状态 (0:在售, 1:交易中, 2:已售出, 3:下架)
     */
    private Integer status;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 发布时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
