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
public class SentimentRequest {

    private List<Instance> instances;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Instance {
        private String text;
    }

    public static SentimentRequest of(String text) {
        return SentimentRequest.builder()
                .instances(List.of(Instance.builder().text(text).build()))
                .build();
    }
}