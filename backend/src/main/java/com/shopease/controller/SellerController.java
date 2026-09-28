package com.shopease.controller;

import com.shopease.dto.ApiResponse;
import com.shopease.dto.DashboardStats;
import com.shopease.dto.ProductRequest;
import com.shopease.model.Product;
import com.shopease.model.User;
import com.shopease.service.DashboardService;
import com.shopease.service.ProductService;
import com.shopease.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerController {

    private final DashboardService dashboardService;
    private final ProductService productService;
    private final UserService userService;

    private String getUserId(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return user.getId();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboardStats(Authentication authentication) {
        DashboardStats stats = dashboardService.getSellerStats(getUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", stats));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse> getSellerProducts(Authentication authentication) {
        List<Product> products = productService.getProductsBySeller(getUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Seller products retrieved", products));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse> createProduct(
            Authentication authentication,
            @Valid @RequestBody ProductRequest request) {
        Product product = productService.createProduct(request, getUserId(authentication));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse> updateProduct(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(id, request, getUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(
            Authentication authentication,
            @PathVariable String id) {
        productService.deleteProduct(id, getUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }
}
