package com.shopease.service;

import com.shopease.dto.ReviewRequest;
import com.shopease.exception.BadRequestException;
import com.shopease.model.Review;
import com.shopease.model.User;
import com.shopease.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final UserService userService;

    public List<Review> getProductReviews(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Review addReview(String userId, ReviewRequest request) {
        if (reviewRepository.existsByProductIdAndUserId(request.getProductId(), userId)) {
            throw new BadRequestException("You have already reviewed this product");
        }

        User user = userService.getUserById(userId);

        Review review = Review.builder()
                .productId(request.getProductId())
                .userId(userId)
                .userName(user.getName())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);
        updateProductRating(request.getProductId());

        return savedReview;
    }

    private void updateProductRating(String productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            productService.updateProductRating(productId, avgRating, reviews.size());
        }
    }
}
