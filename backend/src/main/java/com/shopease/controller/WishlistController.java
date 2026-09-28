package com.shopease.controller;

import com.shopease.dto.ApiResponse;
import com.shopease.model.Product;
import com.shopease.model.User;
import com.shopease.model.Wishlist;
import com.shopease.service.UserService;
import com.shopease.service.WishlistService;
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
    private final UserService userService;

    private String getUserId(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getWishlist(Authentication authentication) {
        List<Product> products = wishlistService.getWishlistProducts(getUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Wishlist retrieved", products));
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<ApiResponse> addToWishlist(
            Authentication authentication,
            @PathVariable String productId) {
        Wishlist wishlist = wishlistService.addToWishlist(getUserId(authentication), productId);
        return ResponseEntity.ok(ApiResponse.success("Added to wishlist", wishlist));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse> removeFromWishlist(
            Authentication authentication,
            @PathVariable String productId) {
        Wishlist wishlist = wishlistService.removeFromWishlist(getUserId(authentication), productId);
        return ResponseEntity.ok(ApiResponse.success("Removed from wishlist", wishlist));
    }

    @PostMapping("/move-to-cart/{productId}")
    public ResponseEntity<ApiResponse> moveToCart(
            Authentication authentication,
            @PathVariable String productId) {
        wishlistService.moveToCart(getUserId(authentication), productId);
        return ResponseEntity.ok(ApiResponse.success("Moved to cart"));
    }
}
