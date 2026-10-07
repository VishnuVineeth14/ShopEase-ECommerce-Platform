package com.shopease.order.controller;

import com.shopease.order.dto.ApiResponse;
import com.shopease.order.dto.DashboardStats;
import com.shopease.order.security.AuthenticatedUser;
import com.shopease.order.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/customer/dashboard")
    public ResponseEntity<ApiResponse<DashboardStats>> getCustomerDashboard(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        DashboardStats stats = dashboardService.getCustomerStats(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/seller/dashboard")
    public ResponseEntity<ApiResponse<DashboardStats>> getSellerDashboard(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        DashboardStats stats = dashboardService.getSellerStats(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/admin/dashboard")
    public ResponseEntity<ApiResponse<DashboardStats>> getAdminDashboard() {
        DashboardStats stats = dashboardService.getAdminStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
