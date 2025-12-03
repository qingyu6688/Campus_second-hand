package com.campus.service;

import com.campus.entity.BizChat;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 私聊消息 Service
 */
public interface BizChatService extends IService<BizChat> {
    java.util.List<com.campus.vo.ChatListVo> selectRecentChats(Long userId);
}
