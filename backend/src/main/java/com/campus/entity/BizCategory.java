package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类
 */
@Data
@TableName("biz_category")
public class BizCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 状态 (1:启用, 0:禁用)
     */
    private Integer status;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
}
