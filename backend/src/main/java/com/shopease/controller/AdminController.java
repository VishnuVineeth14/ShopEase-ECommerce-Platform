package com.shopease.controller;

import com.shopease.dto.ApiResponse;
import com.shopease.dto.CategoryRequest;
import com.shopease.dto.DashboardStats;
import com.shopease.dto.UserDto;
import com.shopease.model.Category;
import com.shopease.model.Order;
import com.shopease.service.CategoryService;
import com.shopease.service.DashboardService;
import com.shopease.service.OrderService;
import com.shopease.service.ProductService;
import com.shopease.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        DashboardStats stats = dashboardService.getAdminStats();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard stats retrieved", stats));
    }

    // USER MANAGEMENT
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search) {
        List<UserDto> users;
        if (search != null && !search.isBlank()) {
            users = userService.searchUsers(search);
        } else if (role != null && !role.isBlank()) {
            users = userService.getUsersByRole(role);
        } else {
            users = userService.getAllUsers();
        }
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse> toggleUserStatus(@PathVariable String id) {
        UserDto user = userService.toggleUserStatus(id);
        return ResponseEntity.ok(ApiResponse.success("User status updated", user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    // CATEGORY MANAGEMENT
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryRequest request) {
        Category category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    // PRODUCT MANAGEMENT
    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable String id) {
        productService.adminDeleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }

    // ORDER MANAGEMENT
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("All orders retrieved", orders));
    }
}
