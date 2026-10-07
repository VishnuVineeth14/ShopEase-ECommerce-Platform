package com.shopease.cart.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopease.cart.dto.ProductDto;
import com.shopease.cart.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductServiceClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ProductServiceClient(@Value("${app.services.product:http://localhost:8082}") String productServiceUrl,
                                ObjectMapper objectMapper) {
        this.restClient = RestClient.builder()
                .baseUrl(productServiceUrl)
                .build();
        this.objectMapper = objectMapper;
    }

    public ProductDto getProductById(String productId) {
        try {
            String response = restClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            if (root.has("data") && !root.get("data").isNull()) {
                return objectMapper.treeToValue(root.get("data"), ProductDto.class);
            }
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch product: " + productId + " (" + e.getMessage() + ")");
        }
    }
}
