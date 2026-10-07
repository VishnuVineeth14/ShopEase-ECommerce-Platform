package com.shopease.order.controller;

import com.shopease.order.dto.ApiResponse;
import com.shopease.order.dto.CheckoutRequest;
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
@RequestMapping("/api/orders/customer")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Order>> checkout(
            Authentication authentication,
            @Valid @RequestBody CheckoutRequest request
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Order order = orderService.placeOrder(user.getId(), user.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.ok("Order placed successfully", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getCustomerOrders(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        List<Order> orders = orderService.getCustomerOrders(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getCustomerOrder(
            Authentication authentication,
            @PathVariable String orderId
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Order order = orderService.getOrderByIdAndCustomer(orderId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            Authentication authentication,
            @PathVariable String orderId
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Order order = orderService.customerCancelOrder(orderId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Order cancelled successfully", order));
    }
}
