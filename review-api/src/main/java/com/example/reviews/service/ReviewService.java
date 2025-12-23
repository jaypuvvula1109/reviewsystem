package com.example.reviews.service;

import com.example.reviews.dto.CreateReviewRequest;
import com.example.reviews.model.Review;
import com.example.reviews.model.Review.SentimentAnalysis;
import com.example.reviews.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SentimentService sentimentService;

    public Review createReview(CreateReviewRequest request) {
        log.info("Creating review for product: {} by user: {}",
                request.getProductId(), request.getUserId());

        // Analyze sentiment
        String textToAnalyze = request.getTitle() + ". " + request.getContent();
        SentimentAnalysis sentiment = sentimentService.analyzeSentiment(textToAnalyze);

        // Build review entity
        Review review = Review.builder()
                .productId(request.getProductId())
                .userId(request.getUserId())
                .rating(request.getRating())
                .title(request.getTitle())
                .content(request.getContent())
                .sentiment(sentiment)
                .createdAt(Instant.now())
                .build();

        // Save to MongoDB
        Review savedReview = reviewRepository.save(review);

        log.info("Review created - ID: {}, Sentiment: {} (score: {})",
                savedReview.getId(),
                sentiment.getSentiment(),
                sentiment.getScore());

        return savedReview;
    }

    public Optional<Review> getReviewById(String id) {
        log.info("Fetching review with ID: {}", id);

        Optional<Review> review = reviewRepository.findById(id);

        if (review.isPresent()) {
            log.info("Review found: {}", id);
        } else {
            log.warn("Review not found: {}", id);
        }

        return review;
    }
}

