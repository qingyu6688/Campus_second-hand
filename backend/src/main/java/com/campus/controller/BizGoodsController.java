package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.BizGoods;
import com.campus.service.BizGoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/api/goods")
public class BizGoodsController {

    @Autowired
    private BizGoodsService bizGoodsService;

    /**
     * 分页获取商品列表接口
     * 支持按关键词搜索和分类筛选
     * 返回包含商品详细信息和卖家信息的分页结果
     */
    @Operation(summary = "分页获取商品列表") // Swagger接口文档注解，说明接口功能
    @GetMapping("/list") // GET请求映射，指定接口路径
    public Result<Page<com.campus.vo.GoodsVo>> list(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sellerId) {

        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BizGoods> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(BizGoods::getIsDeleted, 0)
                .eq(BizGoods::getStatus, 0); // 只显示在售

        if (org.springframework.util.StringUtils.hasText(keyword)) {
            queryWrapper.and(w -> w.like(BizGoods::getTitle, keyword)
                    .or().like(BizGoods::getDescription, keyword));
        }

        if (categoryId != null) {
            queryWrapper.eq(BizGoods::getCategoryId, categoryId);
        }

        if (sellerId != null) {
            queryWrapper.eq(BizGoods::getSellerId, sellerId);
        }

        queryWrapper.orderByDesc(BizGoods::getCreateTime);

        Page<BizGoods> goodsPage = bizGoodsService.page(new Page<>(page, size), queryWrapper);

        // 转换为 VO
        Page<com.campus.vo.GoodsVo> voPage = new Page<>();
        org.springframework.beans.BeanUtils.copyProperties(goodsPage, voPage);

        java.util.List<com.campus.vo.GoodsVo> voList = goodsPage.getRecords().stream().map(goods -> {
            com.campus.vo.GoodsVo vo = new com.campus.vo.GoodsVo();
            org.springframework.beans.BeanUtils.copyProperties(goods, vo);

            // 填充卖家信息
            com.campus.entity.SysUser seller = sysUserService.getById(goods.getSellerId());
            if (seller != null) {
                seller.setPassword(null); // 脱敏
                vo.setSeller(seller);
            }
            return vo;
        }).collect(java.util.stream.Collectors.toList());
        voPage.setRecords(voList);

        return Result.success(voPage);
    }

    @Autowired
    private com.campus.service.SysUserService sysUserService;

    /**
     * 获取商品详情接口
     * 根据商品ID获取商品详细信息及卖家信息
     * 
     * @param id 商品ID
     * @return 返回商品详情和卖家信息的Map集合
     */
    @Operation(summary = "获取商品详情")
    @GetMapping("/detail/{id}")
    public Result<java.util.Map<String, Object>> getById(@PathVariable Long id) {
        // 根据ID查询商品信息
        BizGoods goods = bizGoodsService.getById(id);
        // 如果商品不存在，返回错误信息
        if (goods == null) {
            return Result.error("商品不存在");
        }

        // 增加浏览量
        // 如果浏览量为空则设为1，否则在原有基础上加1
        goods.setViewCount(goods.getViewCount() == null ? 1 : goods.getViewCount() + 1);
        // 更新商品信息（主要是更新浏览量）
        bizGoodsService.updateById(goods);

        // 根据卖家ID查询卖家信息
        com.campus.entity.SysUser seller = sysUserService.getById(goods.getSellerId());
        // 脱敏处理，隐藏密码信息
        if (seller != null) {
            seller.setPassword(null);
        }

        // 构造返回结果Map
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        // 添加商品信息
        map.put("goods", goods);
        // 添加卖家信息
        map.put("seller", seller);

        // 返回成功结果，包含商品和卖家信息
        return Result.success(map);
    }

    /**
     * 获取当前用户发布的商品列表接口
     * 该接口用于分页查询当前用户已发布的商品信息
     *
     * @param page    页码，默认值为1
     * @param size    每页大小，默认值为10
     * @param request HTTP请求对象，用于获取用户ID
     * @return 返回商品分页结果
     */
    @Operation(summary = "获取我的发布的商品") // Swagger接口文档注解，描述接口功能
    @GetMapping("/my") // GET请求映射，指定接口路径
    public Result<Page<BizGoods>> myGoods(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BizGoods> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(BizGoods::getSellerId, userId)
                .eq(BizGoods::getIsDeleted, 0)
                .orderByDesc(BizGoods::getCreateTime);

        return Result.success(bizGoodsService.page(new Page<>(page, size), queryWrapper));
    }

    /**
     * 发布/修改商品接口
     * 该接口用于商家发布新商品或修改已有商品信息
     * 
     * @param goods   商品信息对象，包含商品的所有属性
     * @param request HTTP请求对象，用于从Token中获取用户信息
     * @return 返回操作结果，包含操作是否成功及相关信息
     */
    @Operation(summary = "发布/修改商品")
    @PostMapping("/publish")
    public Result<Boolean> publish(@RequestBody BizGoods goods, jakarta.servlet.http.HttpServletRequest request) {
        // 从 Token 中获取当前用户 ID
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());
        goods.setSellerId(userId);

        // 如果是修改，检查权限
        if (goods.getId() != null) {
            BizGoods dbGoods = bizGoodsService.getById(goods.getId());
            if (dbGoods == null) {
                return Result.error("商品不存在");
            }
            if (!dbGoods.getSellerId().equals(userId)) {
                return Result.error("无权修改他人商品");
            }
        }

        return Result.success(bizGoodsService.saveOrUpdate(goods));
    }

    @Operation(summary = "管理员分页获取商品列表")
    @GetMapping("/admin/page")
    public Result<Page<com.campus.vo.GoodsVo>> adminPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BizGoods> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(BizGoods::getIsDeleted, 0);

        if (org.springframework.util.StringUtils.hasText(keyword)) {
            queryWrapper.and(w -> w.like(BizGoods::getTitle, keyword)
                    .or().like(BizGoods::getDescription, keyword));
        }

        if (categoryId != null) {
            queryWrapper.eq(BizGoods::getCategoryId, categoryId);
        }

        if (status != null) {
            queryWrapper.eq(BizGoods::getStatus, status);
        }

        queryWrapper.orderByDesc(BizGoods::getCreateTime);

        Page<BizGoods> goodsPage = bizGoodsService.page(new Page<>(page, size), queryWrapper);

        // Convert to VO
        Page<com.campus.vo.GoodsVo> voPage = new Page<>();
        org.springframework.beans.BeanUtils.copyProperties(goodsPage, voPage);

        java.util.List<com.campus.vo.GoodsVo> voList = goodsPage.getRecords().stream().map(goods -> {
            com.campus.vo.GoodsVo vo = new com.campus.vo.GoodsVo();
            org.springframework.beans.BeanUtils.copyProperties(goods, vo);
            com.campus.entity.SysUser seller = sysUserService.getById(goods.getSellerId());
            if (seller != null) {
                seller.setPassword(null);
                vo.setSeller(seller);
            }
            return vo;
        }).collect(java.util.stream.Collectors.toList());
        voPage.setRecords(voList);

        return Result.success(voPage);
    }

    @Operation(summary = "管理员删除商品")
    @DeleteMapping("/admin/{id}")
    public Result<Boolean> adminRemove(@PathVariable Long id) {
        return Result.success(bizGoodsService.removeById(id));
    }

    @Operation(summary = "管理员更新商品状态")
    @PostMapping("/admin/status")
    public Result<Boolean> adminUpdateStatus(@RequestBody BizGoods goods) {
        if (goods.getId() == null || goods.getStatus() == null) {
            return Result.error("参数错误");
        }
        com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<BizGoods> updateWrapper = new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
        updateWrapper.eq(BizGoods::getId, goods.getId())
                .set(BizGoods::getStatus, goods.getStatus());
        return Result.success(bizGoodsService.update(updateWrapper));
    }
}
