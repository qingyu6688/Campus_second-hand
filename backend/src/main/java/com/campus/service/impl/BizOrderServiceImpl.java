package com.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.BizOrder;
import com.campus.mapper.BizOrderMapper;
import com.campus.service.BizOrderService;
import org.springframework.stereotype.Service;

/**
 * 订单服务实现类
 */
@Service
public class BizOrderServiceImpl extends ServiceImpl<BizOrderMapper, BizOrder> implements BizOrderService {
}
