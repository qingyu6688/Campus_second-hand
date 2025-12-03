package com.campus.service.impl;

import com.campus.entity.BizChat;
import com.campus.mapper.BizChatMapper;
import com.campus.service.BizChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 私聊消息 Service实现类
 */
@Service
public class BizChatServiceImpl extends ServiceImpl<BizChatMapper, BizChat> implements BizChatService {

    @Override
    public java.util.List<com.campus.vo.ChatListVo> selectRecentChats(Long userId) {
        return baseMapper.selectRecentChats(userId);
    }
}
