package com.shopease.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

@RestController
public class GatewayProxyController {

    @Value("${gateway.services.user:http://localhost:8081}")
    private String userServiceUrl;

    @Value("${gateway.services.product:http://localhost:8082}")
    private String productServiceUrl;

    @Value("${gateway.services.cart:http://localhost:8083}")
    private String cartServiceUrl;

    @Value("${gateway.services.order:http://localhost:8084}")
    private String orderServiceUrl;

    private final RestTemplate restTemplate;

    public GatewayProxyController() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    @RequestMapping("/api/**")
    public ResponseEntity<?> proxyRequest(
            @RequestBody(required = false) byte[] body,
            HttpMethod method,
            HttpServletRequest request
    ) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String targetServiceUrl = determineTargetService(path);

        if (targetServiceUrl == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "No microservice mapped for path: " + path));
        }

        String targetUrl = targetServiceUrl + path + (query != null ? "?" + query : "");

        try {
            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (!headerName.equalsIgnoreCase("host") &&
                    !headerName.equalsIgnoreCase("content-length") &&
                    !headerName.equalsIgnoreCase("connection")) {
                    Enumeration<String> values = request.getHeaders(headerName);
                    while (values.hasMoreElements()) {
                        headers.add(headerName, values.nextElement());
                    }
                }
            }

            HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    URI.create(targetUrl),
                    method,
                    entity,
                    byte[].class
            );

            HttpHeaders responseHeaders = new HttpHeaders();
            response.getHeaders().forEach((name, vals) -> {
                if (!name.equalsIgnoreCase("transfer-encoding") &&
                    !name.equalsIgnoreCase("connection")) {
                    responseHeaders.put(name, vals);
                }
            });

            return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());

        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .headers(ex.getResponseHeaders())
                    .body(ex.getResponseBodyAsByteArray());
        } catch (ResourceAccessException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "success", false,
                            "message", "Service unavailable at " + targetServiceUrl + ". Ensure the corresponding microservice is running."
                    ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Gateway forwarding error: " + ex.getMessage()
                    ));
        }
    }

    private String determineTargetService(String path) {
        // User & Auth Service
        if (path.startsWith("/api/auth") ||
            path.startsWith("/api/admin/users") ||
            path.startsWith("/api/users") ||
            path.equals("/api/customer/profile")) {
            return userServiceUrl;
        }

        // Cart & Wishlist Service
        if (path.startsWith("/api/cart") ||
            path.startsWith("/api/wishlist")) {
            return cartServiceUrl;
        }

        // Order & Payment & Dashboards Service
        if (path.startsWith("/api/orders") ||
            path.startsWith("/api/admin/orders") ||
            path.equals("/api/customer/dashboard") ||
            path.equals("/api/seller/dashboard") ||
            path.equals("/api/admin/dashboard")) {
            return orderServiceUrl;
        }

        // Product & Catalog Service
        if (path.startsWith("/api/products") ||
            path.startsWith("/api/categories") ||
            path.startsWith("/api/admin/categories") ||
            path.startsWith("/api/seller/products") ||
            path.startsWith("/api/reviews")) {
            return productServiceUrl;
        }

        return null;
    }
}
