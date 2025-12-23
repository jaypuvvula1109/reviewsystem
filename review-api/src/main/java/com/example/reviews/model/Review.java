
package com.example.reviews.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class Review {

    @Id
    private String id;

    private String productId;

    private String userId;

    private int rating;

    private String title;

    private String content;

    private SentimentAnalysis sentiment;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SentimentAnalysis {
        private String sentiment;
        private double score;
        private double confidence;
        private List<String> categories;
        private String summary;
    }
}