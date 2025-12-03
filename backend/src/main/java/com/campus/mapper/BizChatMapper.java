package com.campus.mapper;

import com.campus.entity.BizChat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 私聊消息 Mapper
 */
public interface BizChatMapper extends BaseMapper<BizChat> {

    @org.apache.ibatis.annotations.Select("""
                SELECT
                    t.target_user_id as targetUserId,
                    u.nickname as targetUserNickname,
                    u.avatar as targetUserAvatar,
                    c.content as lastContent,
                    c.msg_type as msgType,
                    c.create_time as lastTime,
                    (SELECT COUNT(*) FROM biz_chat WHERE sender_id = t.target_user_id AND receiver_id = #{userId} AND is_read = 0) as unreadCount
                FROM (
                    SELECT
                        CASE
                            WHEN sender_id = #{userId} THEN receiver_id
                            ELSE sender_id
                        END as target_user_id,
                        MAX(id) as max_id
                    FROM biz_chat
                    WHERE sender_id = #{userId} OR receiver_id = #{userId}
                    GROUP BY
                        CASE
                            WHEN sender_id = #{userId} THEN receiver_id
                            ELSE sender_id
                        END
                ) t
                LEFT JOIN biz_chat c ON t.max_id = c.id
                LEFT JOIN sys_user u ON t.target_user_id = u.id
                ORDER BY c.create_time DESC
            """)
    java.util.List<com.campus.vo.ChatListVo> selectRecentChats(
            @org.apache.ibatis.annotations.Param("userId") Long userId);
}
