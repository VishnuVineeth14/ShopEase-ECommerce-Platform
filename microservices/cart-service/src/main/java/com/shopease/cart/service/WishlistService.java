package com.shopease.cart.service;

import com.shopease.cart.client.ProductServiceClient;
import com.shopease.cart.dto.AddToCartRequest;
import com.shopease.cart.dto.ProductDto;
import com.shopease.cart.model.Wishlist;
import com.shopease.cart.repository.WishlistRepository;
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
    private final ProductServiceClient productServiceClient;
    private final CartService cartService;

    public Wishlist getWishlist(String userId) {
        return wishlistRepository.findByUserId(userId)
                .orElse(Wishlist.builder()
                        .userId(userId)
                        .productIds(new ArrayList<>())
                        .updatedAt(LocalDateTime.now())
                        .build());
    }

    public List<ProductDto> getWishlistProducts(String userId) {
        Wishlist wishlist = getWishlist(userId);
        return wishlist.getProductIds().stream()
                .map(id -> {
                    try {
                        return productServiceClient.getProductById(id);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }

    public Wishlist addToWishlist(String userId, String productId) {
        // Validate product exists
        productServiceClient.getProductById(productId);

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
        cartService.addToCart(userId, AddToCartRequest.builder()
                .productId(productId)
                .quantity(1)
                .build());

        removeFromWishlist(userId, productId);
    }

    public int getWishlistCount(String userId) {
        return getWishlist(userId).getProductIds().size();
    }
}
