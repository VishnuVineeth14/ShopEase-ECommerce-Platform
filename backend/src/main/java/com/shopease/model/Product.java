package com.shopease.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    private double price;

    private int stock;

    private String imageUrl;

    private String categoryId;

    private String categoryName;

    private String sellerId;

    private String sellerName;

    @Builder.Default
    private double rating = 0.0;

    @Builder.Default
    private int reviewCount = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
