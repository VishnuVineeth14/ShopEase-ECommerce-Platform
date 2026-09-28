package com.shopease.controller;

import com.shopease.dto.ApiResponse;
import com.shopease.dto.CheckoutRequest;
import com.shopease.dto.OrderStatusRequest;
import com.shopease.model.Order;
import com.shopease.model.User;
import com.shopease.service.OrderService;
import com.shopease.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    private String getUserId(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return user.getId();
    }

    // CUSTOMER ROUTES
    @PostMapping("/customer/checkout")
    public ResponseEntity<ApiResponse> placeOrder(
            Authentication authentication,
            @Valid @RequestBody CheckoutRequest request) {
        Order order = orderService.placeOrder(getUserId(authentication), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", order));
    }

    @GetMapping("/customer")
    public ResponseEntity<ApiResponse> getCustomerOrders(Authentication authentication) {
        List<Order> orders = orderService.getCustomerOrders(getUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved", orders));
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<ApiResponse> getCustomerOrderById(
            Authentication authentication,
            @PathVariable String id) {
        Order order = orderService.getOrderByIdAndCustomer(id, getUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Order details retrieved", order));
    }

    @PutMapping("/customer/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelOrder(
            Authentication authentication,
            @PathVariable String id) {
        Order order = orderService.customerCancelOrder(id, getUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", order));
    }

    // SELLER ROUTES
    @GetMapping("/seller")
    public ResponseEntity<ApiResponse> getSellerOrders(Authentication authentication) {
        List<Order> orders = orderService.getSellerOrders(getUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Seller orders retrieved", orders));
    }

    @PutMapping("/seller/{id}/status")
    public ResponseEntity<ApiResponse> updateSellerOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody OrderStatusRequest request) {
        Order order = orderService.updateOrderStatus(id, request.getOrderStatus());
        return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
    }
}
