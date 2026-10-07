package com.shopease.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private String productId;
    private String productName;
    private String imageUrl;
    private double price;
    private int quantity;
    private String sellerId;
    private double subtotal;
}
