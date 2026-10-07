package com.shopease.order.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ProductServiceClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ProductServiceClient(@Value("${app.services.product:http://localhost:8082}") String productServiceUrl,
                                ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().baseUrl(productServiceUrl).build();
        this.objectMapper = objectMapper;
    }

    public void deductStock(Map<String, Integer> items) {
        restClient.post()
                .uri("/api/products/internal/deduct-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("items", items))
                .retrieve()
                .toBodilessEntity();
    }

    public void restoreStock(Map<String, Integer> items) {
        try {
            restClient.post()
                    .uri("/api/products/internal/restore-stock")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("items", items))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignored) {
        }
    }

    public Map<String, Object> getSellerProductStats(String sellerId) {
        try {
            String response = restClient.get()
                    .uri("/api/products/internal/stats/seller/{sellerId}", sellerId)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            if (root.has("data") && !root.get("data").isNull()) {
                return objectMapper.convertValue(root.get("data"), Map.class);
            }
        } catch (Exception ignored) {
        }
        return Map.of("totalProducts", 0L, "lowStockProducts", 0L);
    }

    public long getTotalProductCount() {
        try {
            String response = restClient.get()
                    .uri("/api/products/internal/stats/total")
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            if (root.has("data") && !root.get("data").isNull()) {
                return root.get("data").get("totalProducts").asLong(0);
            }
        } catch (Exception ignored) {
        }
        return 0L;
    }
}
