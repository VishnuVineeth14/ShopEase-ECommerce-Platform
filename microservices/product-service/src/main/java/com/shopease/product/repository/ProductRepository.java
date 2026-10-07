package com.shopease.product.repository;

import com.shopease.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategoryId(String categoryId);
    List<Product> findBySellerId(String sellerId);
    List<Product> findByNameContainingIgnoreCase(String name);
    long countBySellerId(String sellerId);
    List<Product> findBySellerIdAndStockLessThanEqual(String sellerId, int stockThreshold);
}
