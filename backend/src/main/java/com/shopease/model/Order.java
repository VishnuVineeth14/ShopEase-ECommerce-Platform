package com.shopease.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String customerId;

    private String customerName;

    private String customerEmail;

    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    private double totalAmount;

    private String address;

    private String phone;

    private PaymentMethod paymentMethod;

    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}
