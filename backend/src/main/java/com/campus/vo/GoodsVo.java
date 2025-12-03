package com.campus.vo;

import com.campus.entity.BizGoods;
import com.campus.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsVo extends BizGoods {
    private SysUser seller;
}
