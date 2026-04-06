package com.allweather.repository;

import com.allweather.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByIsApprovedTrueOrderByCreatedAtDesc();

    List<Review> findAllByOrderByCreatedAtDesc();
}
