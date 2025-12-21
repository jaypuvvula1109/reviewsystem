package com.example.phi.service;

import com.example.phi.dto.PredictResponse.SentimentPrediction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.ModelParameters;
import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaOutput;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PhiModelService {

    private static final Logger log = LoggerFactory.getLogger(PhiModelService.class);
    private static final Pattern JSON_PATTERN = Pattern.compile("\\{[^{}]*\\}", Pattern.DOTALL);

    @Value("${model.path}")
    private String modelPath;

    @Value("${model.threads}")
    private int nThreads;

    @Value("${model.context-size}")
    private int contextSize;

    private LlamaModel model;
    private boolean ready = false;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        log.info("Loading model from: {}", modelPath);
        log.info("Using {} threads, context size {}", nThreads, contextSize);

        try {
            ModelParameters params = new ModelParameters()
                    .setModelFilePath(modelPath)
                    .setNThreads(nThreads)
                    .setNCtx(contextSize)
                    .setNGpuLayers(0);

            model = new LlamaModel(params);
            ready = true;
            log.info("Model loaded successfully!");
        } catch (Exception e) {
            log.error("Failed to load model: {}", e.getMessage(), e);
            throw new RuntimeException("Model loading failed", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (model != null) {
            model.close();
        }
    }

    public boolean isReady() {
        return ready;
    }

    public SentimentPrediction analyzeReview(String reviewText) {
        if (!ready) {
            return errorResponse("Model not ready");
        }

        String prompt = buildPrompt(reviewText);

        try {
        InferenceParameters inferParams = new InferenceParameters(prompt)
            .setTemperature(0.1f)
            .setNPredict(200)
            .setStopStrings(new String[]{"<|end|>", "<|user|>"});

            StringBuilder response = new StringBuilder();
            for (LlamaOutput output : model.generate(inferParams)) {
                response.append(output.text);
            }

            return parseResponse(response.toString());

        } catch (Exception e) {
            log.error("Inference error: {}", e.getMessage(), e);
            return errorResponse("Inference error: " + e.getMessage());
        }
    }

    private String buildPrompt(String reviewText) {
        return String.format("""
                <|system|>
                You are a review analyzer. Analyze reviews and respond with ONLY valid JSON, no other text.<|end|>
                <|user|>
                Analyze this product review:

                "%s"

                Respond with ONLY this JSON structure (no markdown, no explanation):
                {"sentiment": "positive" or "negative" or "neutral", "score": 0.0 to 1.0, "confidence": 0.0 to 1.0, "categories": ["from: product_quality, customer_service, shipping, pricing, usability, general"], "summary": "one sentence"}<|end|>
                <|assistant|>
                """, reviewText);
    }

    private SentimentPrediction parseResponse(String responseText) {
        try {
            String cleaned = responseText.trim()
                    .replaceAll("^```json\\s*", "")
                    .replaceAll("\\s*```$", "");

            Matcher matcher = JSON_PATTERN.matcher(cleaned);
            if (matcher.find()) {
                JsonNode json = objectMapper.readTree(matcher.group());

                String sentiment = json.path("sentiment").asText("neutral").toLowerCase();
                if (!List.of("positive", "negative", "neutral").contains(sentiment)) {
                    sentiment = "neutral";
                }

                double score = Math.max(0.0, Math.min(1.0, json.path("score").asDouble(0.5)));
                double confidence = Math.max(0.0, Math.min(1.0, json.path("confidence").asDouble(0.5)));

                List<String> categories = new ArrayList<>();
                JsonNode categoriesNode = json.path("categories");
                if (categoriesNode.isArray()) {
                    categoriesNode.forEach(node -> categories.add(node.asText()));
                }
                if (categories.isEmpty()) {
                    categories.add("general");
                }

                String summary = json.path("summary").asText("");

                return SentimentPrediction.builder()
                        .sentiment(sentiment)
                        .score(Math.round(score * 1000.0) / 1000.0)
                        .confidence(Math.round(confidence * 1000.0) / 1000.0)
                        .categories(categories)
                        .summary(summary.length() > 200 ? summary.substring(0, 200) : summary)
                        .build();
            } else {
                return errorResponse("No JSON found in response");
            }

        } catch (Exception e) {
            log.error("Parse error: {}", e.getMessage());
            return errorResponse("Parse error: " + e.getMessage());
        }
    }

    private SentimentPrediction errorResponse(String message) {
        return SentimentPrediction.builder()
                .sentiment("unknown")
                .score(0.5)
                .confidence(0.0)
                .categories(List.of("general"))
                .summary("")
                .error(message)
                .build();
    }
}