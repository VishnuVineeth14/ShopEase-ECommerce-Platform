package com.shopease.service;

import com.shopease.dto.DashboardStats;
import com.shopease.model.Order;
import com.shopease.model.OrderStatus;
import com.shopease.model.Role;
import com.shopease.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;
    private final WishlistService wishlistService;

    public DashboardStats getCustomerStats(String userId) {
        List<Order> orders = orderService.getCustomerOrders(userId);
        long pending = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.PENDING).count();
        long completed = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED).count();

        return DashboardStats.builder()
                .totalOrders((long) orders.size())
                .pendingOrders(pending)
                .completedOrders(completed)
                .cartCount(cartService.getCartCount(userId))
                .wishlistCount(wishlistService.getWishlistCount(userId))
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

        return DashboardStats.builder()
                .totalProducts(productService.countBySeller(sellerId))
                .totalOrders((long) orders.size())
                .pendingOrders(pending)
                .completedOrders(completed)
                .totalSales(sales)
                .lowStockProducts((long) productService.getLowStockProducts(sellerId, 5).size())
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

        return DashboardStats.builder()
                .totalCustomers(userService.countByRole(Role.CUSTOMER))
                .totalSellers(userService.countByRole(Role.SELLER))
                .totalUsers(userService.countByRole(Role.CUSTOMER) + userService.countByRole(Role.SELLER) + userService.countByRole(Role.ADMIN))
                .totalProducts((long) productService.getAllProducts().size())
                .totalOrders((long) orders.size())
                .pendingOrders(pending)
                .totalRevenue(revenue)
                .ordersByStatus(statusCounts)
                .build();
    }
}
