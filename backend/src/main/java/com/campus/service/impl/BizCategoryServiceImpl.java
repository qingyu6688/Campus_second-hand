package com.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.BizCategory;
import com.campus.mapper.BizCategoryMapper;
import com.campus.service.BizCategoryService;
import org.springframework.stereotype.Service;

/**
 * 商品分类服务实现类
 */
@Service
public class BizCategoryServiceImpl extends ServiceImpl<BizCategoryMapper, BizCategory> implements BizCategoryService {
}
