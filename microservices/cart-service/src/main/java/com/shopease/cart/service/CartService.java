package com.shopease.cart.service;

import com.shopease.cart.client.ProductServiceClient;
import com.shopease.cart.dto.AddToCartRequest;
import com.shopease.cart.dto.ProductDto;
import com.shopease.cart.dto.UpdateCartRequest;
import com.shopease.cart.exception.BadRequestException;
import com.shopease.cart.model.Cart;
import com.shopease.cart.model.CartItem;
import com.shopease.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductServiceClient productServiceClient;

    public Cart getCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyCart(userId));
    }

    public Cart addToCart(String userId, AddToCartRequest request) {
        ProductDto product = productServiceClient.getProductById(request.getProductId());

        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Requested quantity (" + request.getQuantity() +
                    ") exceeds available stock (" + product.getStock() + ")");
        }

        Cart cart = getCart(userId);

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            if (product.getStock() < newQuantity) {
                throw new BadRequestException("Cannot add more. Total in cart (" + newQuantity +
                        ") exceeds available stock (" + product.getStock() + ")");
            }

            existingItem.setQuantity(newQuantity);
            existingItem.setSubtotal(existingItem.getPrice() * newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .imageUrl(product.getImageUrl())
                    .price(product.getPrice())
                    .quantity(request.getQuantity())
                    .sellerId(product.getSellerId())
                    .subtotal(product.getPrice() * request.getQuantity())
                    .build();

            cart.getItems().add(newItem);
        }

        recalculateCart(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public Cart updateCartItemQuantity(String userId, UpdateCartRequest request) {
        Cart cart = getCart(userId);

        if (request.getQuantity() == 0) {
            return removeFromCart(userId, request.getProductId());
        }

        ProductDto product = productServiceClient.getProductById(request.getProductId());

        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Requested quantity (" + request.getQuantity() +
                    ") exceeds available stock (" + product.getStock() + ")");
        }

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Product not found in cart"));

        item.setQuantity(request.getQuantity());
        item.setSubtotal(item.getPrice() * request.getQuantity());

        recalculateCart(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public Cart removeFromCart(String userId, String productId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        recalculateCart(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public void clearCart(String userId) {
        Cart cart = getCart(userId);
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        cart.setTotalItems(0);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    public int getCartCount(String userId) {
        return getCart(userId).getTotalItems();
    }

    private Cart createEmptyCart(String userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .items(new ArrayList<>())
                .totalAmount(0.0)
                .totalItems(0)
                .updatedAt(LocalDateTime.now())
                .build();
        return cartRepository.save(cart);
    }

    private void recalculateCart(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
        int count = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        cart.setTotalAmount(Math.round(total * 100.0) / 100.0);
        cart.setTotalItems(count);
    }
}
