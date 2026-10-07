package com.shopease.order.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopease.order.dto.CartDto;
import com.shopease.order.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CartServiceClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public CartServiceClient(@Value("${app.services.cart:http://localhost:8083}") String cartServiceUrl,
                             ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().baseUrl(cartServiceUrl).build();
        this.objectMapper = objectMapper;
    }

    public CartDto getCartByUserId(String userId) {
        try {
            String response = restClient.get()
                    .uri("/api/cart/internal/user/{userId}", userId)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            if (root.has("data") && !root.get("data").isNull()) {
                return objectMapper.treeToValue(root.get("data"), CartDto.class);
            }
            throw new ResourceNotFoundException("Cart not found for user: " + userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch cart: " + e.getMessage());
        }
    }

    public void clearCart(String userId) {
        try {
            restClient.delete()
                    .uri("/api/cart/internal/clear/{userId}", userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignored) {
        }
    }

    public int getCartCount(String userId) {
        try {
            String response = restClient.get()
                    .uri("/api/cart/internal/count/{userId}", userId)
                    .retrieve()
                    .body(String.class);
            JsonNode root = objectMapper.readTree(response);
            if (root.has("data") && !root.get("data").isNull()) {
                return root.get("data").asInt(0);
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getWishlistCount(String userId) {
        try {
            String response = restClient.get()
                    .uri("/api/wishlist/internal/count/{userId}", userId)
                    .retrieve()
                    .body(String.class);
            JsonNode root = objectMapper.readTree(response);
            if (root.has("data") && !root.get("data").isNull()) {
                return root.get("data").asInt(0);
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
