package com.campus.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatListVo {
    private Long targetUserId;
    private String targetUserNickname;
    private String targetUserAvatar;
    private String lastContent;
    private Integer msgType; // 0: text, 1: image
    private LocalDateTime lastTime;
    private Integer unreadCount;
}
