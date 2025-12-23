package com.example.reviews.dto;

import com.example.reviews.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private String id;
    private String productId;
    private String userId;
    private int rating;
    private String title;
    private String content;
    private SentimentResponse sentiment;
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SentimentResponse {
        private String sentiment;
        private double score;
        private double confidence;
        private List<String> categories;
        private String summary;

        public static SentimentResponse fromEntity(Review.SentimentAnalysis entity) {
            if (entity == null) {
                return null;
            }
            return SentimentResponse.builder()
                    .sentiment(entity.getSentiment())
                    .score(entity.getScore())
                    .confidence(entity.getConfidence())
                    .categories(entity.getCategories())
                    .summary(entity.getSummary())
                    .build();
        }
    }

    public static ReviewResponse fromEntity(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .sentiment(SentimentResponse.fromEntity(review.getSentiment()))
                .createdAt(review.getCreatedAt())
                .build();
    }
}