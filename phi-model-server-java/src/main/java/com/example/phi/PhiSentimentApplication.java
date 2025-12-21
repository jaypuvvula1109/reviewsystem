package com.example.phi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration.class
})
public class PhiSentimentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhiSentimentApplication.class, args);
    }
}
