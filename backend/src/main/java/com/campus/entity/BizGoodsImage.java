package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品图片
 */
@Data
@TableName("biz_goods_image")
public class BizGoodsImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 图片地址
     */
    private String url;

    /**
     * 排序 (越小越靠前，0为主图)
     */
    private Integer sortOrder;
}
