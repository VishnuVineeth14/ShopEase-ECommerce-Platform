package com.shopease.product.service;

import com.shopease.product.dto.ProductRequest;
import com.shopease.product.exception.BadRequestException;
import com.shopease.product.exception.ForbiddenException;
import com.shopease.product.exception.ResourceNotFoundException;
import com.shopease.product.model.Category;
import com.shopease.product.model.Product;
import com.shopease.product.repository.CategoryRepository;
import com.shopease.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getAllProducts(String categoryId, Double minPrice, Double maxPrice, String query, String sort) {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(p -> categoryId == null || categoryId.isBlank() || p.getCategoryId().equals(categoryId))
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .filter(p -> {
                    if (query == null || query.isBlank()) return true;
                    String q = query.toLowerCase();
                    return p.getName().toLowerCase().contains(q) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(q));
                })
                .sorted((p1, p2) -> {
                    if (sort == null) return 0;
                    return switch (sort) {
                        case "price_asc" -> Double.compare(p1.getPrice(), p2.getPrice());
                        case "price_desc" -> Double.compare(p2.getPrice(), p1.getPrice());
                        case "rating" -> Double.compare(p2.getRating(), p1.getRating());
                        case "newest" -> p2.getCreatedAt().compareTo(p1.getCreatedAt());
                        default -> 0;
                    };
                })
                .collect(Collectors.toList());
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.isBlank()) {
            return productRepository.findAll();
        }
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Product> getSellerProducts(String sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public Product createProduct(String sellerId, String sellerName, ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .sellerId(sellerId)
                .sellerName(sellerName)
                .rating(0.0)
                .reviewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return productRepository.save(product);
    }

    public Product updateProduct(String id, String sellerId, ProductRequest request) {
        Product product = getProductById(id);

        if (!product.getSellerId().equals(sellerId)) {
            throw new ForbiddenException("You are not allowed to update this product");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        product.setCategoryId(category.getId());
        product.setCategoryName(category.getName());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    public void deleteProduct(String id, String sellerId) {
        Product product = getProductById(id);

        if (!product.getSellerId().equals(sellerId)) {
            throw new ForbiddenException("You are not allowed to delete this product");
        }

        productRepository.delete(product);
    }

    public synchronized void deductStock(Map<String, Integer> items) {
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            Product product = getProductById(entry.getKey());
            if (product.getStock() < entry.getValue()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }
        }
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            Product product = getProductById(entry.getKey());
            product.setStock(product.getStock() - entry.getValue());
            product.setUpdatedAt(LocalDateTime.now());
            productRepository.save(product);
        }
    }

    public synchronized void restoreStock(Map<String, Integer> items) {
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + entry.getValue());
                product.setUpdatedAt(LocalDateTime.now());
                productRepository.save(product);
            }
        }
    }

    public long countBySeller(String sellerId) {
        return productRepository.countBySellerId(sellerId);
    }

    public List<Product> getLowStockProducts(String sellerId, int threshold) {
        return productRepository.findBySellerIdAndStockLessThanEqual(sellerId, threshold);
    }

    public long getTotalCount() {
        return productRepository.count();
    }
}
