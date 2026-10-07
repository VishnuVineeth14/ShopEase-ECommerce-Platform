package com.shopease.order.controller;

import com.shopease.order.dto.ApiResponse;
import com.shopease.order.dto.OrderStatusUpdateRequest;
import com.shopease.order.model.Order;
import com.shopease.order.security.AuthenticatedUser;
import com.shopease.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/seller")
@RequiredArgsConstructor
public class SellerOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getSellerOrders(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        List<Order> orders = orderService.getSellerOrders(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        Order order = orderService.updateOrderStatus(orderId, request.getOrderStatus());
        return ResponseEntity.ok(ApiResponse.ok("Order status updated successfully", order));
    }
}
