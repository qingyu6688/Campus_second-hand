package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.Result;
import com.campus.entity.BizGoods;
import com.campus.entity.BizOrder;
import com.campus.entity.BizCategory;
import com.campus.service.BizCategoryService;
import com.campus.service.BizGoodsService;
import com.campus.service.BizOrderService;
import com.campus.service.SysUserService;
import com.campus.vo.DashboardVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "控制台管理")
@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private BizGoodsService bizGoodsService;

    @Autowired
    private BizOrderService bizOrderService;

    @Autowired
    private BizCategoryService bizCategoryService;

    @Operation(summary = "获取控制台统计数据")
    @GetMapping("/stats")
    public Result<DashboardVo> stats() {
        DashboardVo vo = new DashboardVo();

        // 1. Basic Counts
        vo.setUserCount(sysUserService.count());
        vo.setGoodsCount(bizGoodsService.count());
        vo.setOrderCount(bizOrderService.count());

        // 2. Total Amount (Completed orders)
        List<BizOrder> completedOrders = bizOrderService.list(new LambdaQueryWrapper<BizOrder>()
                .eq(BizOrder::getStatus, 1));
        BigDecimal totalAmount = completedOrders.stream()
                .map(BizOrder::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalAmount(totalAmount);

        // 3. Recent Orders (Top 5)
        List<BizOrder> recentOrders = bizOrderService.list(new LambdaQueryWrapper<BizOrder>()
                .orderByDesc(BizOrder::getCreateTime)
                .last("LIMIT 5"));
        // Populate goods info for display
        recentOrders.forEach(order -> {
            BizGoods goods = bizGoodsService.getById(order.getGoodsId());
            if (goods != null) {
                order.setGoods(goods);
            }
        });
        vo.setRecentOrders(recentOrders);

        // 4. Category Data (Goods distribution)
        List<BizGoods> allGoods = bizGoodsService.list();
        Map<Long, Long> categoryCountMap = allGoods.stream()
                .collect(Collectors.groupingBy(BizGoods::getCategoryId, Collectors.counting()));

        List<BizCategory> categories = bizCategoryService.list();
        Map<Long, String> categoryNameMap = categories.stream()
                .collect(Collectors.toMap(BizCategory::getId, BizCategory::getName));

        List<Map<String, Object>> categoryData = new ArrayList<>();
        categoryCountMap.forEach((catId, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", categoryNameMap.getOrDefault(catId, "未知分类"));
            item.put("value", count);
            categoryData.add(item);
        });
        vo.setCategoryData(categoryData);

        // 5. Order Trend (Last 7 days)
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);

        List<BizOrder> last7DaysOrders = bizOrderService.list(new LambdaQueryWrapper<BizOrder>()
                .ge(BizOrder::getCreateTime, sevenDaysAgo.atStartOfDay()));

        Map<String, Long> dateCountMap = last7DaysOrders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getCreateTime().toLocalDate().format(DateTimeFormatter.ofPattern("MM-dd")),
                        Collectors.counting()));

        List<Map<String, Object>> orderTrend = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = sevenDaysAgo.plusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("MM-dd"));
            Map<String, Object> item = new HashMap<>();
            item.put("date", dateStr);
            item.put("count", dateCountMap.getOrDefault(dateStr, 0L));
            orderTrend.add(item);
        }
        vo.setOrderTrend(orderTrend);

        return Result.success(vo);
    }
}
