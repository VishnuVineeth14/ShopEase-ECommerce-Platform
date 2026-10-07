package com.shopease.product.controller;

import com.shopease.product.dto.ApiResponse;
import com.shopease.product.dto.StockUpdateRequest;
import com.shopease.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products/internal")
@RequiredArgsConstructor
public class InternalProductController {

    private final ProductService productService;

    @PostMapping("/deduct-stock")
    public ResponseEntity<ApiResponse<Void>> deductStock(@RequestBody StockUpdateRequest request) {
        productService.deductStock(request.getItems());
        return ResponseEntity.ok(ApiResponse.ok("Stock deducted successfully", null));
    }

    @PostMapping("/restore-stock")
    public ResponseEntity<ApiResponse<Void>> restoreStock(@RequestBody StockUpdateRequest request) {
        productService.restoreStock(request.getItems());
        return ResponseEntity.ok(ApiResponse.ok("Stock restored successfully", null));
    }

    @GetMapping("/stats/seller/{sellerId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSellerProductStats(@PathVariable String sellerId) {
        Map<String, Object> map = new HashMap<>();
        map.put("totalProducts", productService.countBySeller(sellerId));
        map.put("lowStockProducts", (long) productService.getLowStockProducts(sellerId, 5).size());
        return ResponseEntity.ok(ApiResponse.ok(map));
    }

    @GetMapping("/stats/total")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getTotalProductStats() {
        Map<String, Long> map = new HashMap<>();
        map.put("totalProducts", productService.getTotalCount());
        return ResponseEntity.ok(ApiResponse.ok(map));
    }
}
