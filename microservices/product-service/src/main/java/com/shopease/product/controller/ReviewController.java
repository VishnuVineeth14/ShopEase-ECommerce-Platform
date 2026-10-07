package com.shopease.product.controller;

import com.shopease.product.dto.ApiResponse;
import com.shopease.product.dto.ReviewRequest;
import com.shopease.product.model.Review;
import com.shopease.product.security.AuthenticatedUser;
import com.shopease.product.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<Review>>> getReviewsByProduct(@PathVariable String productId) {
        List<Review> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(ApiResponse.ok(reviews));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Review>> addReview(
            Authentication authentication,
            @Valid @RequestBody ReviewRequest request
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Review review = reviewService.addReview(user.getId(), user.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.ok("Review added successfully", review));
    }
}
