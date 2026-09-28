package com.shopease.controller;

import com.shopease.dto.ApiResponse;
import com.shopease.dto.CartItemRequest;
import com.shopease.model.Cart;
import com.shopease.model.User;
import com.shopease.service.CartService;
import com.shopease.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    private String getUserId(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getCart(Authentication authentication) {
        Cart cart = cartService.getCart(getUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved", cart));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addToCart(
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {
        Cart cart = cartService.addToCart(getUserId(authentication), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", cart));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateQuantity(
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {
        Cart cart = cartService.updateCartItemQuantity(getUserId(authentication), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Cart updated", cart));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse> removeFromCart(
            Authentication authentication,
            @PathVariable String productId) {
        Cart cart = cartService.removeFromCart(getUserId(authentication), productId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse> clearCart(Authentication authentication) {
        cartService.clearCart(getUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Cart cleared"));
    }
}
