package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.BizFavorite;
import com.campus.service.BizFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏控制器
 */
@Tag(name = "收藏管理")
@RestController
@RequestMapping("/api/favorite")
public class BizFavoriteController {

    @Autowired
    private BizFavoriteService bizFavoriteService;

    @Operation(summary = "获取我的收藏")
    @GetMapping("/list")
    public Result<List<BizFavorite>> list(jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());
        return Result.success(bizFavoriteService.lambdaQuery().eq(BizFavorite::getUserId, userId).list());
    }

    @Operation(summary = "添加/取消收藏")
    @PostMapping("/toggle")
    public Result<Boolean> toggle(@RequestBody BizFavorite favorite, jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());
        favorite.setUserId(userId);

        // 简单实现：存在则删除，不存在则添加
        boolean exists = bizFavoriteService.lambdaQuery()
                .eq(BizFavorite::getUserId, userId)
                .eq(BizFavorite::getGoodsId, favorite.getGoodsId())
                .exists();
        if (exists) {
            bizFavoriteService.lambdaUpdate()
                    .eq(BizFavorite::getUserId, userId)
                    .eq(BizFavorite::getGoodsId, favorite.getGoodsId())
                    .remove();
            return Result.success(false); // 返回false表示当前未收藏
        } else {
            bizFavoriteService.save(favorite);
            return Result.success(true); // 返回true表示当前已收藏
        }
    }

    @Operation(summary = "检查是否收藏")
    @GetMapping("/check/{goodsId}")
    public Result<Boolean> check(@PathVariable Long goodsId, jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.success(false); // 未登录视为未收藏
        }
        Long userId = Long.valueOf(userIdObj.toString());
        boolean exists = bizFavoriteService.lambdaQuery()
                .eq(BizFavorite::getUserId, userId)
                .eq(BizFavorite::getGoodsId, goodsId)
                .exists();
        return Result.success(exists);
    }

    @Autowired
    private com.campus.mapper.BizFavoriteMapper bizFavoriteMapper;

    @Operation(summary = "获取我的收藏商品")
    @GetMapping("/my-goods")
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<com.campus.entity.BizGoods>> myGoods(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.campus.entity.BizGoods> pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                page, size);
        return Result.success(bizFavoriteMapper.selectUserFavoriteGoods(pageParam, userId));
    }
}
