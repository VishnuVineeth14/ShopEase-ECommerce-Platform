package com.shopease.controller;

import com.shopease.dto.ApiResponse;
import com.shopease.dto.ReviewRequest;
import com.shopease.model.Review;
import com.shopease.model.User;
import com.shopease.service.ReviewService;
import com.shopease.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    private String getUserId(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return user.getId();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse> getProductReviews(@PathVariable String productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully", reviews));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addReview(
            Authentication authentication,
            @Valid @RequestBody ReviewRequest request) {

        Review review = reviewService.addReview(getUserId(authentication), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review added successfully", review));
    }
}
