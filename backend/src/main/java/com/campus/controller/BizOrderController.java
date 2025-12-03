package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.BizOrder;
import com.campus.service.BizOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/order")
public class BizOrderController {

    @Autowired
    private BizOrderService bizOrderService;

    @Operation(summary = "分页获取订单列表")
    @GetMapping("/list")
    public Result<Page<BizOrder>> list(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(bizOrderService.page(new Page<>(page, size)));
    }

    @Autowired
    private com.campus.service.BizGoodsService bizGoodsService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<Long> create(@RequestBody java.util.Map<String, Object> params,
            jakarta.servlet.http.HttpServletRequest request) {
        Object goodsIdObj = params.get("goodsId");
        if (goodsIdObj == null) {
            return Result.error("商品ID不能为空");
        }
        Long goodsId = Long.valueOf(goodsIdObj.toString());

        String address = (String) params.get("address");
        String remark = (String) params.get("remark");

        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long buyerId = Long.valueOf(userIdObj.toString());

        // 1. Check Goods
        com.campus.entity.BizGoods goods = bizGoodsService.getById(goodsId);
        if (goods == null) {
            return Result.error("商品不存在");
        }
        if (goods.getStatus() != 0) { // 0: On Sale
            return Result.error("商品已售出或下架");
        }
        if (goods.getSellerId().equals(buyerId)) {
            return Result.error("不能购买自己的商品");
        }

        // 2. Create Order
        BizOrder order = new BizOrder();
        order.setOrderNo(cn.hutool.core.util.IdUtil.getSnowflakeNextIdStr()); // Use Hutool or UUID
        order.setGoodsId(goodsId);
        order.setBuyerId(buyerId);
        order.setSellerId(goods.getSellerId());
        order.setAmount(goods.getPrice());
        order.setStatus(1); // 1: Completed (Simplified flow)
        order.setRemark(remark);

        bizOrderService.save(order);

        // 3. Update Goods Status
        goods.setStatus(2); // 2: Sold
        bizGoodsService.updateById(goods);

        return Result.success(order.getId());
    }

    @Autowired
    private com.campus.service.SysUserService sysUserService;

    @Operation(summary = "获取我的订单")
    @GetMapping("/my")
    public Result<Page<BizOrder>> myOrders(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BizOrder> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(BizOrder::getBuyerId, userId)
                .orderByDesc(BizOrder::getCreateTime);

        Page<BizOrder> orderPage = bizOrderService.page(new Page<>(page, size), queryWrapper);

        // Populate goods and seller info
        orderPage.getRecords().forEach(order -> {
            com.campus.entity.BizGoods goods = bizGoodsService.getById(order.getGoodsId());
            if (goods != null) {
                order.setGoods(goods);
                com.campus.entity.SysUser seller = sysUserService.getById(goods.getSellerId());
                if (seller != null) {
                    seller.setPassword(null);
                    order.setSeller(seller);
                }
            }
        });

        return Result.success(orderPage);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/detail/{id}")
    public Result<BizOrder> detail(@PathVariable Long id) {
        BizOrder order = bizOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // Populate goods and seller info
        com.campus.entity.BizGoods goods = bizGoodsService.getById(order.getGoodsId());
        if (goods != null) {
            order.setGoods(goods);
            com.campus.entity.SysUser seller = sysUserService.getById(goods.getSellerId());
            if (seller != null) {
                seller.setPassword(null);
                order.setSeller(seller);
            }
        }

        return Result.success(order);
    }

    @Operation(summary = "获取我卖出的订单")
    @GetMapping("/sold")
    public Result<Page<BizOrder>> mySoldOrders(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BizOrder> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(BizOrder::getSellerId, userId)
                .orderByDesc(BizOrder::getCreateTime);

        Page<BizOrder> orderPage = bizOrderService.page(new Page<>(page, size), queryWrapper);

        // Populate goods and buyer info
        orderPage.getRecords().forEach(order -> {
            com.campus.entity.BizGoods goods = bizGoodsService.getById(order.getGoodsId());
            if (goods != null) {
                order.setGoods(goods);
                // Note: BizOrder entity might need a 'buyer' field or we reuse 'seller' field
                // temporarily or add a map?
                // Actually BizOrder has buyerId. We should probably add a 'buyer' field to
                // BizOrder entity or just put it in a map if we were using VO.
                // But since we are returning BizOrder entity directly (which is not ideal but
                // consistent with current code),
                // we might need to check if BizOrder has a buyer field.
                // Let's check BizOrder entity first.
            }

            // Fetch buyer info
            com.campus.entity.SysUser buyer = sysUserService.getById(order.getBuyerId());
            if (buyer != null) {
                buyer.setPassword(null);
                order.setBuyer(buyer);
            }
        });

        return Result.success(orderPage);
    }

    @Operation(summary = "管理员分页获取订单列表")
    @GetMapping("/admin/page")
    public Result<Page<BizOrder>> adminPage(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status) {

        Page<BizOrder> orderPage = new Page<>(page, size);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BizOrder> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        if (cn.hutool.core.util.StrUtil.isNotBlank(orderNo)) {
            wrapper.like(BizOrder::getOrderNo, orderNo);
        }
        if (status != null) {
            wrapper.eq(BizOrder::getStatus, status);
        }

        wrapper.orderByDesc(BizOrder::getCreateTime);

        bizOrderService.page(orderPage, wrapper);

        // Populate details
        orderPage.getRecords().forEach(order -> {
            // Goods info
            com.campus.entity.BizGoods goods = bizGoodsService.getById(order.getGoodsId());
            if (goods != null) {
                order.setGoods(goods);
            }

            // Seller info
            com.campus.entity.SysUser seller = sysUserService.getById(order.getSellerId());
            if (seller != null) {
                seller.setPassword(null);
                order.setSeller(seller);
            }

            // Buyer info
            com.campus.entity.SysUser buyer = sysUserService.getById(order.getBuyerId());
            if (buyer != null) {
                buyer.setPassword(null);
                order.setBuyer(buyer);
            }
        });

        return Result.success(orderPage);
    }

    @Operation(summary = "管理员删除订单")
    @DeleteMapping("/admin/{id}")
    public Result<Boolean> adminRemove(@PathVariable Long id) {
        return Result.success(bizOrderService.removeById(id));
    }
}
