package com.shopease.product.service;

import com.shopease.product.dto.ReviewRequest;
import com.shopease.product.model.Product;
import com.shopease.product.model.Review;
import com.shopease.product.repository.ProductRepository;
import com.shopease.product.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public List<Review> getReviewsByProduct(String productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public Review addReview(String customerId, String customerName, ReviewRequest request) {
        Product product = productService.getProductById(request.getProductId());

        Review review = Review.builder()
                .productId(product.getId())
                .customerId(customerId)
                .customerName(customerName)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Review saved = reviewRepository.save(review);

        // Update product average rating
        List<Review> allReviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(product.getId());
        double avg = allReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        product.setRating(Math.round(avg * 10.0) / 10.0);
        product.setReviewCount(allReviews.size());
        productRepository.save(product);

        return saved;
    }
}
