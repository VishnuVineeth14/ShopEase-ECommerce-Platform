package com.shopease.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
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
    private double rating;
    private int reviewCount;
}
