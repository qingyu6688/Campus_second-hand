package com.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.BizFavorite;
import com.campus.mapper.BizFavoriteMapper;
import com.campus.service.BizFavoriteService;
import org.springframework.stereotype.Service;

/**
 * 收藏服务实现类
 */
@Service
public class BizFavoriteServiceImpl extends ServiceImpl<BizFavoriteMapper, BizFavorite> implements BizFavoriteService {
}
