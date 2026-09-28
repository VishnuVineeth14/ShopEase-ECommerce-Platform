package com.shopease.service;

import com.shopease.dto.ProductRequest;
import com.shopease.exception.ForbiddenException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.Category;
import com.shopease.model.Product;
import com.shopease.model.User;
import com.shopease.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public List<Product> getProductsBySeller(String sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public List<Product> getProductsByCategory(String categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Product> filterProducts(String categoryId, Double minPrice, Double maxPrice, String sort) {
        List<Product> products;

        if (categoryId != null && !categoryId.isEmpty()) {
            if (minPrice != null && maxPrice != null) {
                products = productRepository.findByCategoryIdAndPriceBetween(categoryId, minPrice, maxPrice);
            } else {
                products = productRepository.findByCategoryId(categoryId);
            }
        } else if (minPrice != null && maxPrice != null) {
            products = productRepository.findByPriceBetween(minPrice, maxPrice);
        } else {
            products = productRepository.findAll();
        }

        if (sort != null) {
            switch (sort) {
                case "price_asc":
                    products.sort(Comparator.comparingDouble(Product::getPrice));
                    break;
                case "price_desc":
                    products.sort(Comparator.comparingDouble(Product::getPrice).reversed());
                    break;
                case "newest":
                    products.sort(Comparator.comparing(Product::getCreatedAt).reversed());
                    break;
                case "rating":
                    products.sort(Comparator.comparingDouble(Product::getRating).reversed());
                    break;
            }
        }

        return products;
    }

    public Product createProduct(ProductRequest request, String sellerId) {
        Category category = categoryService.getCategoryById(request.getCategoryId());
        User seller = userService.getUserById(sellerId);

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl() != null ? request.getImageUrl() :
                        "https://via.placeholder.com/300x300?text=" + request.getName().replace(" ", "+"))
                .categoryId(category.getId())
                .categoryName(category.getName())
                .sellerId(sellerId)
                .sellerName(seller.getName())
                .rating(0.0)
                .reviewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return productRepository.save(product);
    }

    public Product updateProduct(String productId, ProductRequest request, String sellerId) {
        Product product = getProductById(productId);

        if (!product.getSellerId().equals(sellerId)) {
            throw new ForbiddenException("You can only modify your own products");
        }

        Category category = categoryService.getCategoryById(request.getCategoryId());

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategoryId(category.getId());
        product.setCategoryName(category.getName());
        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            product.setImageUrl(request.getImageUrl());
        }
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    public void deleteProduct(String productId, String sellerId) {
        Product product = getProductById(productId);
        if (!product.getSellerId().equals(sellerId)) {
            throw new ForbiddenException("You can only delete your own products");
        }
        productRepository.delete(product);
    }

    public void adminDeleteProduct(String productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    public long countBySeller(String sellerId) {
        return productRepository.countBySellerId(sellerId);
    }

    public List<Product> getLowStockProducts(String sellerId, int threshold) {
        return productRepository.findBySellerIdAndStockLessThan(sellerId, threshold);
    }

    public void updateProductRating(String productId, double avgRating, int reviewCount) {
        Product product = getProductById(productId);
        product.setRating(avgRating);
        product.setReviewCount(reviewCount);
        productRepository.save(product);
    }
}
