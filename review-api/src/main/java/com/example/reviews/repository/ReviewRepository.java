package com.example.reviews.repository;

import com.example.reviews.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    Optional<Review> findById(String id);
}