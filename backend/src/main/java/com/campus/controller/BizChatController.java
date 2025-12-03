package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.Result;
import com.campus.entity.BizChat;
import com.campus.service.BizChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 私聊控制器
 */
@Tag(name = "私聊管理")
@RestController
@RequestMapping("/api/chat")
public class BizChatController {

    @Autowired
    private BizChatService bizChatService;

    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public Result<Boolean> send(@RequestBody BizChat chat, jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());
        chat.setSenderId(userId);
        chat.setCreateTime(LocalDateTime.now());
        chat.setIsRead(false);

        return Result.success(bizChatService.save(chat));
    }

    @Operation(summary = "获取聊天记录")
    @GetMapping("/history/{targetUserId}")
    public Result<List<BizChat>> history(@PathVariable Long targetUserId,
            jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());

        // 查询当前用户和目标用户之间的所有消息
        LambdaQueryWrapper<BizChat> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .eq(BizChat::getSenderId, userId).eq(BizChat::getReceiverId, targetUserId)
                .or()
                .eq(BizChat::getSenderId, targetUserId).eq(BizChat::getReceiverId, userId))
                .orderByAsc(BizChat::getCreateTime);

        return Result.success(bizChatService.list(wrapper));
    }

    @Operation(summary = "获取最近聊天列表")
    @GetMapping("/list")
    public Result<List<com.campus.vo.ChatListVo>> list(jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());

        return Result.success(bizChatService.selectRecentChats(userId));
    }

    @Operation(summary = "标记已读")
    @PostMapping("/read/{senderId}")
    public Result<Boolean> read(@PathVariable Long senderId, jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());

        // 将目标用户发给当前用户的消息标记为已读
        // update biz_chat set is_read = 1 where sender_id = senderId and receiver_id =
        // userId and is_read = 0
        boolean update = bizChatService.lambdaUpdate()
                .set(BizChat::getIsRead, true)
                .eq(BizChat::getSenderId, senderId)
                .eq(BizChat::getReceiverId, userId)
                .eq(BizChat::getIsRead, false)
                .update();

        return Result.success(update);
    }

    @Autowired
    private com.campus.service.SysUserService sysUserService;

    @Operation(summary = "管理员分页获取聊天列表")
    @GetMapping("/admin/page")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<BizChat>> adminPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BizChat> chatPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                page, size);
        LambdaQueryWrapper<BizChat> wrapper = new LambdaQueryWrapper<>();

        if (cn.hutool.core.util.StrUtil.isNotBlank(keyword)) {
            wrapper.like(BizChat::getContent, keyword);
        }

        wrapper.orderByDesc(BizChat::getCreateTime);

        bizChatService.page(chatPage, wrapper);

        // Populate details (Sender, Receiver)
        chatPage.getRecords().forEach(chat -> {
            // Sender info
            com.campus.entity.SysUser sender = sysUserService.getById(chat.getSenderId());
            if (sender != null) {
                sender.setPassword(null);
                chat.setSender(sender);
            }

            // Receiver info
            com.campus.entity.SysUser receiver = sysUserService.getById(chat.getReceiverId());
            if (receiver != null) {
                receiver.setPassword(null);
                chat.setReceiver(receiver);
            }
        });

        return Result.success(chatPage);
    }

    @Operation(summary = "管理员删除聊天记录")
    @DeleteMapping("/admin/{id}")
    public Result<Boolean> adminRemove(@PathVariable Long id) {
        return Result.success(bizChatService.removeById(id));
    }
}
