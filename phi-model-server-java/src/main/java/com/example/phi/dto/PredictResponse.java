package com.example.phi.dto;

import java.util.List;

public class PredictResponse {
    private List<SentimentPrediction> predictions;

    public PredictResponse(List<SentimentPrediction> predictions) {
        this.predictions = predictions;
    }

    public List<SentimentPrediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<SentimentPrediction> predictions) {
        this.predictions = predictions;
    }

    public static class SentimentPrediction {
        private String sentiment;
        private double score;
        private double confidence;
        private List<String> categories;
        private String summary;
        private String error;

        // Builder pattern
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final SentimentPrediction prediction = new SentimentPrediction();

            public Builder sentiment(String sentiment) {
                prediction.sentiment = sentiment;
                return this;
            }

            public Builder score(double score) {
                prediction.score = score;
                return this;
            }

            public Builder confidence(double confidence) {
                prediction.confidence = confidence;
                return this;
            }

            public Builder categories(List<String> categories) {
                prediction.categories = categories;
                return this;
            }

            public Builder summary(String summary) {
                prediction.summary = summary;
                return this;
            }

            public Builder error(String error) {
                prediction.error = error;
                return this;
            }

            public SentimentPrediction build() {
                return prediction;
            }
        }

        // Getters
        public String getSentiment() { return sentiment; }
        public double getScore() { return score; }
        public double getConfidence() { return confidence; }
        public List<String> getCategories() { return categories; }
        public String getSummary() { return summary; }
        public String getError() { return error; }
    }
}