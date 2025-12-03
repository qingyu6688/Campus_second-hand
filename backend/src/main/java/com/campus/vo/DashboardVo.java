package com.campus.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardVo {
    private Long userCount;
    private Long goodsCount;
    private Long orderCount;
    private BigDecimal totalAmount;

    private List<Map<String, Object>> categoryData; // {name: 'Digital', value: 10}
    private List<Map<String, Object>> orderTrend; // {date: '2023-10-01', count: 5, amount: 100}
    private List<com.campus.entity.BizOrder> recentOrders;
}
