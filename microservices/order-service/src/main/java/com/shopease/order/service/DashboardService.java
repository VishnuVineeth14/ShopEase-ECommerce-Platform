package com.shopease.order.service;

import com.shopease.order.client.CartServiceClient;
import com.shopease.order.client.ProductServiceClient;
import com.shopease.order.client.UserServiceClient;
import com.shopease.order.dto.DashboardStats;
import com.shopease.order.model.Order;
import com.shopease.order.model.OrderStatus;
import com.shopease.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final CartServiceClient cartServiceClient;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    public DashboardStats getCustomerStats(String userId) {
        List<Order> orders = orderService.getCustomerOrders(userId);
        long pending = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.PENDING).count();
        long completed = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED).count();

        return DashboardStats.builder()
                .totalOrders((long) orders.size())
                .pendingOrders(pending)
                .completedOrders(completed)
                .cartCount(cartServiceClient.getCartCount(userId))
                .wishlistCount(cartServiceClient.getWishlistCount(userId))
                .build();
    }

    public DashboardStats getSellerStats(String sellerId) {
        List<Order> orders = orderService.getSellerOrders(sellerId);
        long pending = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.PENDING).count();
        long completed = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED).count();
        double sales = orders.stream()
                .filter(o -> o.getOrderStatus() != OrderStatus.CANCELLED)
                .mapToDouble(Order::getTotalAmount)
                .sum();

        Map<String, Object> productStats = productServiceClient.getSellerProductStats(sellerId);
        Long totalProducts = ((Number) productStats.getOrDefault("totalProducts", 0L)).longValue();
        Long lowStock = ((Number) productStats.getOrDefault("lowStockProducts", 0L)).longValue();

        return DashboardStats.builder()
                .totalProducts(totalProducts)
                .totalOrders((long) orders.size())
                .pendingOrders(pending)
                .completedOrders(completed)
                .totalSales(sales)
                .lowStockProducts(lowStock)
                .build();
    }

    public DashboardStats getAdminStats() {
        List<Order> orders = orderService.getAllOrders();
        long pending = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.PENDING).count();
        double revenue = orders.stream()
                .filter(o -> o.getOrderStatus() != OrderStatus.CANCELLED)
                .mapToDouble(Order::getTotalAmount)
                .sum();

        Map<String, Long> statusCounts = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            statusCounts.put(status.name(), orderRepository.countByOrderStatus(status));
        }

        Map<String, Long> userStats = userServiceClient.getUserStats();
        Long customers = userStats.getOrDefault("totalCustomers", 0L);
        Long sellers = userStats.getOrDefault("totalSellers", 0L);
        Long totalUsers = userStats.getOrDefault("totalUsers", 0L);

        long totalProducts = productServiceClient.getTotalProductCount();

        return DashboardStats.builder()
                .totalCustomers(customers)
                .totalSellers(sellers)
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalOrders((long) orders.size())
                .pendingOrders(pending)
                .totalRevenue(revenue)
                .ordersByStatus(statusCounts)
                .build();
    }
}
