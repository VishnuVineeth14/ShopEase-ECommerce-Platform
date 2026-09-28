package com.shopease.repository;

import com.shopease.model.Order;
import com.shopease.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByCustomerId(String customerId);

    List<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    long countByCustomerId(String customerId);

    long countByCustomerIdAndOrderStatus(String customerId, OrderStatus orderStatus);

    long countByOrderStatus(OrderStatus orderStatus);

    List<Order> findAllByOrderByCreatedAtDesc();
}
