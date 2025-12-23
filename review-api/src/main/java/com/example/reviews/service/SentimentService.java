package com.example.reviews.service;

import com.example.reviews.dto.SentimentRequest;
import com.example.reviews.dto.SentimentServiceResponse;
import com.example.reviews.dto.SentimentServiceResponse.Prediction;
import com.example.reviews.model.Review.SentimentAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class SentimentService {

    private final WebClient webClient;
    private final String modelName;
    private final int timeoutSeconds;

    public SentimentService(
            @Value("${sentiment.service.url}") String serviceUrl,
            @Value("${sentiment.service.model-name}") String modelName,
            @Value("${sentiment.service.timeout-seconds}") int timeoutSeconds) {

        this.webClient = WebClient.builder()
                .baseUrl(serviceUrl)
                .build();
        this.modelName = modelName;
        this.timeoutSeconds = timeoutSeconds;

        log.info("SentimentService initialized - URL: {}, Model: {}, Timeout: {}s",
                serviceUrl, modelName, timeoutSeconds);
    }

    public SentimentAnalysis analyzeSentiment(String text) {
        log.info("Analyzing sentiment for text: {}...",
                text.substring(0, Math.min(50, text.length())));

        try {
            SentimentRequest request = SentimentRequest.of(text);

            SentimentServiceResponse response = webClient.post()
                    .uri("/v1/models/{modelName}:predict", modelName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(SentimentServiceResponse.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            if (response == null || response.getFirstPrediction() == null) {
                log.warn("Empty response from sentiment service");
                return createFallbackSentiment("Empty response from service");
            }

            Prediction prediction = response.getFirstPrediction();

            if (prediction.hasError()) {
                log.warn("Sentiment service returned error: {}", prediction.getError());
                return createFallbackSentiment(prediction.getError());
            }

            log.info("Sentiment analysis complete: {} (score: {})",
                    prediction.getSentiment(), prediction.getScore());

            return SentimentAnalysis.builder()
                    .sentiment(prediction.getSentiment())
                    .score(prediction.getScore())
                    .confidence(prediction.getConfidence())
                    .categories(prediction.getCategories())
                    .summary(prediction.getSummary())
                    .build();

        } catch (WebClientException e) {
            log.error("Failed to call sentiment service: {}", e.getMessage());
            return createFallbackSentiment("Service unavailable: " + e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error during sentiment analysis: {}", e.getMessage(), e);
            return createFallbackSentiment("Unexpected error: " + e.getMessage());
        }
    }

    private SentimentAnalysis createFallbackSentiment(String errorMessage) {
        log.warn("Using fallback sentiment due to: {}", errorMessage);
        return SentimentAnalysis.builder()
                .sentiment("unknown")
                .score(0.5)
                .confidence(0.0)
                .categories(List.of("general"))
                .summary("Sentiment analysis unavailable: " + errorMessage)
                .build();
    }
}
