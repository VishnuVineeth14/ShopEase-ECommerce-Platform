package com.shopease.controller;

import com.shopease.dto.ApiResponse;
import com.shopease.dto.DashboardStats;
import com.shopease.model.User;
import com.shopease.service.DashboardService;
import com.shopease.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboardStats(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        DashboardStats stats = dashboardService.getCustomerStats(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", stats));
    }
}
