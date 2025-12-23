package com.example.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentServiceResponse {

    private List<Prediction> predictions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prediction {
        private String sentiment;
        private double score;
        private double confidence;
        private List<String> categories;
        private String summary;
        private String error;

        public boolean hasError() {
            return error != null && !error.isBlank();
        }
    }

    public Prediction getFirstPrediction() {
        if (predictions == null || predictions.isEmpty()) {
            return null;
        }
        return predictions.get(0);
    }
}
