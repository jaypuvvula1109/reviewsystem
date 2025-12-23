package com.example.reviews.controller;

import com.example.reviews.dto.CreateReviewRequest;
import com.example.reviews.dto.ReviewResponse;
import com.example.reviews.model.Review;
import com.example.reviews.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        log.info("POST /reviews - Product: {}, User: {}",
                request.getProductId(), request.getUserId());

        Review review = reviewService.createReview(request);
        ReviewResponse response = ReviewResponse.fromEntity(review);

        log.info("Review created successfully - ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable String id) {
        log.info("GET /reviews/{}", id);

        return reviewService.getReviewById(id)
                .map(review -> {
                    ReviewResponse response = ReviewResponse.fromEntity(review);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("Review not found: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}

