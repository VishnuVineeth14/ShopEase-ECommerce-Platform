package com.shopease.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {

    // Customer stats
    private Long totalOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Integer cartCount;
    private Integer wishlistCount;

    // Seller stats
    private Long totalProducts;
    private Double totalSales;
    private Long lowStockProducts;

    // Admin stats
    private Long totalCustomers;
    private Long totalSellers;
    private Double totalRevenue;
    private Long totalUsers;

    private Map<String, Long> ordersByStatus;
}
