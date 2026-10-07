package com.shopease.order.repository;

import com.shopease.order.model.Order;
import com.shopease.order.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    List<Order> findAllByOrderByCreatedAtDesc();
    long countByOrderStatus(OrderStatus orderStatus);
}
