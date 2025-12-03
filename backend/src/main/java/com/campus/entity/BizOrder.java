package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易订单
 */
@Data
@TableName("biz_order")
public class BizOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 买家ID
     */
    private Long buyerId;

    /**
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 成交金额
     */
    private BigDecimal amount;

    /**
     * 状态 (0:待交易, 1:已完成, 2:已取消)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 支付方式 (1:微信, 2:支付宝, 3:线下)
     */
    @TableField(exist = false)
    private Integer paymentMethod;

    /**
     * 交易方式 (1:自提, 2:送货)
     */
    @TableField(exist = false)
    private Integer deliveryMethod;

    /**
     * 交易地点/地址
     */
    @TableField(exist = false)
    private String address;

    /**
     * 联系电话
     */
    @TableField(exist = false)
    private String contactPhone;

    @TableField(exist = false)
    private BizGoods goods;

    @TableField(exist = false)
    private com.campus.entity.SysUser seller;

    @TableField(exist = false)
    private com.campus.entity.SysUser buyer;
}
