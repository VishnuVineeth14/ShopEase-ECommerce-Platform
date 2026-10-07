package com.shopease.order.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class UserServiceClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public UserServiceClient(@Value("${app.services.user:http://localhost:8081}") String userServiceUrl,
                             ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().baseUrl(userServiceUrl).build();
        this.objectMapper = objectMapper;
    }

    public Map<String, Long> getUserStats() {
        try {
            String response = restClient.get()
                    .uri("/api/users/stats")
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            if (root.has("data") && !root.get("data").isNull()) {
                return objectMapper.convertValue(root.get("data"), Map.class);
            }
        } catch (Exception ignored) {
        }
        return Map.of("totalCustomers", 0L, "totalSellers", 0L, "totalUsers", 0L);
    }
}
