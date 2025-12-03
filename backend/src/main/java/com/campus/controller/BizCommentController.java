package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.Result;
import com.campus.entity.BizComment;
import com.campus.entity.SysUser;
import com.campus.service.BizCommentService;
import com.campus.service.SysUserService;
import com.campus.vo.CommentVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 留言控制器
 */
@Tag(name = "留言管理")
@RestController
@RequestMapping("/api/comment")
public class BizCommentController {

    @Autowired
    private BizCommentService bizCommentService;

    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取商品留言列表
     */
    @Operation(summary = "获取商品留言列表")
    @GetMapping("/list/{goodsId}")
    public Result<List<CommentVo>> list(@PathVariable Long goodsId) {
        LambdaQueryWrapper<BizComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizComment::getGoodsId, goodsId)
                .orderByDesc(BizComment::getCreateTime);

        List<BizComment> comments = bizCommentService.list(wrapper);

        // 1. Convert all to VO
        List<CommentVo> allVos = comments.stream().map(comment -> {
            CommentVo vo = new CommentVo();
            BeanUtils.copyProperties(comment, vo);

            // 填充留言者信息
            SysUser user = sysUserService.getById(comment.getUserId());
            if (user != null) {
                vo.setUserNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
                vo.setUserAvatar(user.getAvatar());
            }

            // 填充被回复者信息
            if (comment.getReplyUserId() != null) {
                SysUser replyUser = sysUserService.getById(comment.getReplyUserId());
                if (replyUser != null) {
                    vo.setReplyUserNickname(
                            replyUser.getNickname() != null ? replyUser.getNickname() : replyUser.getUsername());
                }
            }

            return vo;
        }).collect(Collectors.toList());

        // 2. Build Tree
        java.util.Map<Long, CommentVo> map = allVos.stream().collect(Collectors.toMap(CommentVo::getId, v -> v));
        List<CommentVo> roots = new java.util.ArrayList<>();

        for (CommentVo vo : allVos) {
            if (vo.getParentId() == null) {
                roots.add(vo);
            } else {
                CommentVo parent = map.get(vo.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new java.util.ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                } else {
                    // Orphaned comment, treat as root
                    roots.add(vo);
                }
            }
        }

        // Sort children by createTime ASC (Oldest first)
        for (CommentVo root : roots) {
            if (root.getChildren() != null) {
                root.getChildren().sort(java.util.Comparator.comparing(BizComment::getCreateTime));
            }
        }

        return Result.success(roots);
    }

    /**
     * 发表留言
     */
    @Operation(summary = "发表留言")
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody BizComment comment, jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());
        comment.setUserId(userId);

        return Result.success(bizCommentService.save(comment));
    }

    /**
     * 删除留言
     */
    @Operation(summary = "删除留言")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());

        BizComment comment = bizCommentService.getById(id);
        if (comment == null) {
            return Result.error("留言不存在");
        }

        // 仅允许删除自己的留言
        if (!comment.getUserId().equals(userId)) {
            return Result.error("无权删除");
        }

        return Result.success(bizCommentService.removeById(id));
    }

    @Autowired
    private com.campus.service.BizGoodsService bizGoodsService;

    @Operation(summary = "管理员分页获取留言列表")
    @GetMapping("/admin/page")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<BizComment>> adminPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BizComment> commentPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                page, size);
        LambdaQueryWrapper<BizComment> wrapper = new LambdaQueryWrapper<>();

        // If keyword exists, flat search
        if (cn.hutool.core.util.StrUtil.isNotBlank(keyword)) {
            wrapper.like(BizComment::getContent, keyword);
            wrapper.orderByDesc(BizComment::getCreateTime);
            bizCommentService.page(commentPage, wrapper);

            // Populate details for flat list
            populateCommentDetails(commentPage.getRecords());
        } else {
            // Tree structure: Query roots first
            wrapper.isNull(BizComment::getParentId);
            wrapper.orderByDesc(BizComment::getCreateTime);
            bizCommentService.page(commentPage, wrapper);

            // Populate details and children for roots
            populateCommentDetails(commentPage.getRecords());

            // Recursively fetch children
            for (BizComment root : commentPage.getRecords()) {
                fetchChildren(root);
            }
        }

        return Result.success(commentPage);
    }

    // Helper to populate User and Goods info
    private void populateCommentDetails(List<BizComment> comments) {
        if (comments == null || comments.isEmpty())
            return;

        for (BizComment comment : comments) {
            // User info
            com.campus.entity.SysUser user = sysUserService.getById(comment.getUserId());
            if (user != null) {
                user.setPassword(null);
                comment.setUser(user);
            }

            // Goods info
            com.campus.entity.BizGoods goods = bizGoodsService.getById(comment.getGoodsId());
            if (goods != null) {
                comment.setGoods(goods);
            }
        }
    }

    // Helper to recursively fetch children
    private void fetchChildren(BizComment parent) {
        LambdaQueryWrapper<BizComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizComment::getParentId, parent.getId());
        wrapper.orderByAsc(BizComment::getCreateTime);
        List<BizComment> children = bizCommentService.list(wrapper);

        if (children != null && !children.isEmpty()) {
            populateCommentDetails(children);
            parent.setChildren(children);
            // Recursively fetch grandchildren
            for (BizComment child : children) {
                fetchChildren(child);
            }
        }
    }

    @Operation(summary = "管理员删除留言")
    @DeleteMapping("/admin/{id}")
    public Result<Boolean> adminRemove(@PathVariable Long id) {
        return Result.success(bizCommentService.removeById(id));
    }
}
