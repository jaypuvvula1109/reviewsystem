package com.example.phi.dto;

import java.util.List;

public class PredictRequest {
    private List<ReviewInstance> instances;

    public List<ReviewInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<ReviewInstance> instances) {
        this.instances = instances;
    }

    public static class ReviewInstance {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}