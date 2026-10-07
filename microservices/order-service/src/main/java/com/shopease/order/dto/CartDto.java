package com.shopease.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private String id;
    private String userId;
    @Builder.Default
    private List<CartItemDto> items = new ArrayList<>();
    private double totalAmount;
    private int totalItems;
}
