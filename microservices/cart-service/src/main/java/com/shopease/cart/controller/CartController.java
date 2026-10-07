package com.shopease.cart.controller;

import com.shopease.cart.dto.AddToCartRequest;
import com.shopease.cart.dto.ApiResponse;
import com.shopease.cart.dto.UpdateCartRequest;
import com.shopease.cart.model.Cart;
import com.shopease.cart.security.AuthenticatedUser;
import com.shopease.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<Cart>> getCart(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Cart cart = cartService.getCart(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Cart>> addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Cart cart = cartService.addToCart(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", cart));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Cart>> updateQuantity(
            Authentication authentication,
            @Valid @RequestBody UpdateCartRequest request
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Cart cart = cartService.updateCartItemQuantity(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Cart updated", cart));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<Cart>> removeFromCart(
            Authentication authentication,
            @PathVariable String productId
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Cart cart = cartService.removeFromCart(user.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", cart));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", null));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getCartCount(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        int count = cartService.getCartCount(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("count", count)));
    }

    // Internal inter-service endpoints
    @GetMapping("/internal/user/{userId}")
    public ResponseEntity<ApiResponse<Cart>> getCartInternal(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(cartService.getCart(userId)));
    }

    @DeleteMapping("/internal/clear/{userId}")
    public ResponseEntity<ApiResponse<Void>> clearCartInternal(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", null));
    }

    @GetMapping("/internal/count/{userId}")
    public ResponseEntity<ApiResponse<Integer>> getCartCountInternal(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(cartService.getCartCount(userId)));
    }
}
