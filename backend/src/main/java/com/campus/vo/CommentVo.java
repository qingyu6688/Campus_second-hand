package com.campus.vo;

import com.campus.entity.BizComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentVo extends BizComment {

    /**
     * 评论者昵称
     */
    private String userNickname;

    /**
     * 评论者头像
     */
    private String userAvatar;

    /**
     * 被回复者昵称
     */
    private String replyUserNickname;
}
