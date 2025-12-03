package com.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.BizComment;
import com.campus.mapper.BizCommentMapper;
import com.campus.service.BizCommentService;
import org.springframework.stereotype.Service;

/**
 * 留言服务实现类
 */
@Service
public class BizCommentServiceImpl extends ServiceImpl<BizCommentMapper, BizComment> implements BizCommentService {
}
