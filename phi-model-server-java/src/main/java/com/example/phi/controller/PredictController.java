package com.example.phi.controller;

import com.example.phi.dto.ModelMetadata;
import com.example.phi.dto.PredictRequest;
import com.example.phi.dto.PredictResponse;
import com.example.phi.dto.PredictResponse.SentimentPrediction;
import com.example.phi.service.PhiModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PredictController {

    private static final Logger log = LoggerFactory.getLogger(PredictController.class);
    private static final String MODEL_NAME = "phi-sentiment";

    private final PhiModelService modelService;

    public PredictController(PhiModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * KServe V1 Predict endpoint
     */
    @PostMapping("/v1/models/{modelName}:predict")
    public ResponseEntity<PredictResponse> predict(
            @PathVariable String modelName,
            @RequestBody PredictRequest request) {

        log.info("Received predict request for model: {}, instances: {}",
                modelName, request.getInstances().size());

        List<SentimentPrediction> predictions = request.getInstances().stream()
                .map(instance -> modelService.analyzeReview(instance.getText()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PredictResponse(predictions));
    }

    /**
     * KServe model metadata endpoint
     */
    @GetMapping("/v1/models/{modelName}")
    public ResponseEntity<ModelMetadata> getModelMetadata(@PathVariable String modelName) {
        return ResponseEntity.ok(new ModelMetadata(MODEL_NAME, "1.0.0", modelService.isReady()));
    }

    /**
     * KServe readiness probe
     */
    @GetMapping("/v1/models/{modelName}/ready")
    public ResponseEntity<Map<String, Boolean>> isReady(@PathVariable String modelName) {
        boolean ready = modelService.isReady();
        if (ready) {
            return ResponseEntity.ok(Map.of("ready", true));
        } else {
            return ResponseEntity.status(503).body(Map.of("ready", false));
        }
    }

    /**
     * Health check
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }
}
