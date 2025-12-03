package com.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.BizGoods;
import com.campus.mapper.BizGoodsMapper;
import com.campus.service.BizGoodsService;
import org.springframework.stereotype.Service;

/**
 * 商品服务实现类
 */
@Service
public class BizGoodsServiceImpl extends ServiceImpl<BizGoodsMapper, BizGoods> implements BizGoodsService {
}
