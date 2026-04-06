package com.allweather.service;

import com.allweather.model.Review;
import com.allweather.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * Submit a new review. Starts as unapproved until admin approves.
     */
    public Review submitReview(Review review) {
        review.setIsApproved(false);
        Review saved = reviewRepository.save(review);
        log.info("New review submitted: ID={}, Customer={}, Rating={}",
                saved.getId(), saved.getCustomerName(), saved.getRating());
        return saved;
    }

    /**
     * Get all approved reviews for the public website.
     */
    @Transactional(readOnly = true)
    public List<Review> getApprovedReviews() {
        return reviewRepository.findByIsApprovedTrueOrderByCreatedAtDesc();
    }

    /**
     * Get all reviews for admin panel.
     */
    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get a single review by ID.
     */
    @Transactional(readOnly = true)
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    /**
     * Approve a review so it shows on the website.
     */
    public Optional<Review> approveReview(Long id) {
        return reviewRepository.findById(id).map(review -> {
            review.setIsApproved(true);
            log.info("Review ID={} approved by admin.", id);
            return reviewRepository.save(review);
        });
    }

    /**
     * Reject/unapprove a review.
     */
    public Optional<Review> rejectReview(Long id) {
        return reviewRepository.findById(id).map(review -> {
            review.setIsApproved(false);
            return reviewRepository.save(review);
        });
    }
}
