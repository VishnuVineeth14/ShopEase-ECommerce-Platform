package com.shopease.service;

import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.Product;
import com.shopease.model.Wishlist;
import com.shopease.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductService productService;
    private final CartService cartService;

    public Wishlist getWishlist(String userId) {
        return wishlistRepository.findByUserId(userId)
                .orElse(Wishlist.builder()
                        .userId(userId)
                        .productIds(new ArrayList<>())
                        .updatedAt(LocalDateTime.now())
                        .build());
    }

    public List<Product> getWishlistProducts(String userId) {
        Wishlist wishlist = getWishlist(userId);
        return wishlist.getProductIds().stream()
                .map(id -> {
                    try {
                        return productService.getProductById(id);
                    } catch (ResourceNotFoundException e) {
                        return null;
                    }
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }

    public Wishlist addToWishlist(String userId, String productId) {
        // Validate product exists
        productService.getProductById(productId);

        Wishlist wishlist = getWishlist(userId);

        if (!wishlist.getProductIds().contains(productId)) {
            wishlist.getProductIds().add(productId);
            wishlist.setUpdatedAt(LocalDateTime.now());
            return wishlistRepository.save(wishlist);
        }

        return wishlist;
    }

    public Wishlist removeFromWishlist(String userId, String productId) {
        Wishlist wishlist = getWishlist(userId);
        wishlist.getProductIds().remove(productId);
        wishlist.setUpdatedAt(LocalDateTime.now());
        return wishlistRepository.save(wishlist);
    }

    public void moveToCart(String userId, String productId) {
        cartService.addToCart(userId, productId, 1);
        removeFromWishlist(userId, productId);
    }

    public int getWishlistCount(String userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElse(null);
        return wishlist != null ? wishlist.getProductIds().size() : 0;
    }
}
