package com.shopease.service;

import com.shopease.dto.CheckoutRequest;
import com.shopease.exception.BadRequestException;
import com.shopease.exception.ForbiddenException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.*;
import com.shopease.repository.OrderRepository;
import com.shopease.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final ProductRepository productRepository;

    @Transactional
    public Order placeOrder(String userId, CheckoutRequest request) {
        User user = userService.getUserById(userId);
        Cart cart = cartService.getCart(userId);

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // Validate stock again right before placing order
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductName()));
            if (product.getStock() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStock());
            }
        }

        List<OrderItem> orderItems = cart.getItems().stream().map(item ->
                OrderItem.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .imageUrl(item.getImageUrl())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .sellerId(item.getSellerId())
                        .subtotal(item.getPrice() * item.getQuantity())
                        .build()
        ).collect(Collectors.toList());

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(request.getPaymentMethod());
        } catch (IllegalArgumentException e) {
            method = PaymentMethod.CASH_ON_DELIVERY;
        }

        PaymentStatus paymentStatus = method == PaymentMethod.CARD_PAYMENT ?
                PaymentStatus.COMPLETED : PaymentStatus.PENDING;

        Order order = Order.builder()
                .customerId(userId)
                .customerName(request.getCustomerName() != null ? request.getCustomerName() : user.getName())
                .customerEmail(user.getEmail())
                .items(orderItems)
                .totalAmount(cart.getTotalAmount())
                .address(request.getAddress())
                .phone(request.getPhone())
                .paymentMethod(method)
                .paymentStatus(paymentStatus)
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Update product stock
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        cartService.clearCart(userId);

        return savedOrder;
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    public Order getOrderByIdAndCustomer(String orderId, String customerId) {
        Order order = getOrderById(orderId);
        if (!order.getCustomerId().equals(customerId)) {
            throw new ForbiddenException("You do not have access to this order");
        }
        return order;
    }

    public List<Order> getCustomerOrders(String customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Order> getSellerOrders(String sellerId) {
        List<Order> allOrders = orderRepository.findAllByOrderByCreatedAtDesc();
        return allOrders.stream()
                .filter(order -> order.getItems().stream().anyMatch(item -> item.getSellerId().equals(sellerId)))
                .map(order -> {
                    // Filter items to only show seller's products in the order
                    List<OrderItem> sellerItems = order.getItems().stream()
                            .filter(item -> item.getSellerId().equals(sellerId))
                            .collect(Collectors.toList());

                    double sellerTotal = sellerItems.stream()
                            .mapToDouble(OrderItem::getSubtotal)
                            .sum();

                    Order sellerOrder = Order.builder()
                            .id(order.getId())
                            .customerId(order.getCustomerId())
                            .customerName(order.getCustomerName())
                            .customerEmail(order.getCustomerEmail())
                            .items(sellerItems)
                            .totalAmount(sellerTotal)
                            .address(order.getAddress())
                            .phone(order.getPhone())
                            .paymentMethod(order.getPaymentMethod())
                            .paymentStatus(order.getPaymentStatus())
                            .orderStatus(order.getOrderStatus())
                            .createdAt(order.getCreatedAt())
                            .updatedAt(order.getUpdatedAt())
                            .build();

                    return sellerOrder;
                })
                .collect(Collectors.toList());
    }

    public Order updateOrderStatus(String orderId, String statusStr) {
        Order order = getOrderById(orderId);

        try {
            OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
            order.setOrderStatus(status);

            if (status == OrderStatus.DELIVERED && order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
                order.setPaymentStatus(PaymentStatus.COMPLETED);
            }

            // Restore stock if cancelled
            if (status == OrderStatus.CANCELLED) {
                for (OrderItem item : order.getItems()) {
                    Product product = productRepository.findById(item.getProductId()).orElse(null);
                    if (product != null) {
                        product.setStock(product.getStock() + item.getQuantity());
                        productRepository.save(product);
                    }
                }
                order.setPaymentStatus(PaymentStatus.REFUNDED);
            }

            order.setUpdatedAt(LocalDateTime.now());
            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + statusStr);
        }
    }

    public Order customerCancelOrder(String orderId, String customerId) {
        Order order = getOrderByIdAndCustomer(orderId, customerId);

        if (order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("Order cannot be cancelled at this stage");
        }

        return updateOrderStatus(orderId, OrderStatus.CANCELLED.name());
    }
}
