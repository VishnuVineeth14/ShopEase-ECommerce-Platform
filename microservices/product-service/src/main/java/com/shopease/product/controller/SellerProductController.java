package com.shopease.product.controller;

import com.shopease.product.dto.ApiResponse;
import com.shopease.product.dto.ProductRequest;
import com.shopease.product.model.Product;
import com.shopease.product.security.AuthenticatedUser;
import com.shopease.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/products")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getSellerProducts(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        List<Product> products = productService.getSellerProducts(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(
            Authentication authentication,
            @Valid @RequestBody ProductRequest request
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Product product = productService.createProduct(user.getId(), user.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.ok("Product created successfully", product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable String id,
            Authentication authentication,
            @Valid @RequestBody ProductRequest request
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Product product = productService.updateProduct(id, user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        productService.deleteProduct(id, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Product deleted successfully", null));
    }
}
