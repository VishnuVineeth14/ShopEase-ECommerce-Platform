package com.shopease.cart.controller;

import com.shopease.cart.dto.ApiResponse;
import com.shopease.cart.dto.ProductDto;
import com.shopease.cart.model.Wishlist;
import com.shopease.cart.security.AuthenticatedUser;
import com.shopease.cart.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDto>>> getWishlist(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        List<ProductDto> products = wishlistService.getWishlistProducts(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<ApiResponse<Wishlist>> addToWishlist(
            Authentication authentication,
            @PathVariable String productId
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Wishlist wishlist = wishlistService.addToWishlist(user.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok("Added to wishlist", wishlist));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<Wishlist>> removeFromWishlist(
            Authentication authentication,
            @PathVariable String productId
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Wishlist wishlist = wishlistService.removeFromWishlist(user.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok("Removed from wishlist", wishlist));
    }

    @PostMapping("/move-to-cart/{productId}")
    public ResponseEntity<ApiResponse<Void>> moveToCart(
            Authentication authentication,
            @PathVariable String productId
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        wishlistService.moveToCart(user.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok("Moved to cart", null));
    }

    @GetMapping("/internal/count/{userId}")
    public ResponseEntity<ApiResponse<Integer>> getWishlistCountInternal(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(wishlistService.getWishlistCount(userId)));
    }
}
