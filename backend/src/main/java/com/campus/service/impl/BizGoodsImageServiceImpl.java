package com.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.BizGoodsImage;
import com.campus.mapper.BizGoodsImageMapper;
import com.campus.service.BizGoodsImageService;
import org.springframework.stereotype.Service;

/**
 * 商品图片服务实现类
 */
@Service
public class BizGoodsImageServiceImpl extends ServiceImpl<BizGoodsImageMapper, BizGoodsImage>
        implements BizGoodsImageService {
}
