package com.shopease.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
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

    private List<OrderItem> items;

    private double totalAmount;

    private String address;

    private String phone;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
