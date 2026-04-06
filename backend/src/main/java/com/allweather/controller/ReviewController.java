package com.allweather.controller;

import com.allweather.model.Review;
import com.allweather.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for customer reviews.
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * GET /api/reviews
     * Get all approved reviews for the public website.
     * Optional ?all=true returns all reviews including pending (admin use).
     */
    @GetMapping
    public ResponseEntity<List<Review>> getReviews(
            @RequestParam(required = false, defaultValue = "false") boolean all) {
        if (all) {
            return ResponseEntity.ok(reviewService.getAllReviews());
        }
        return ResponseEntity.ok(reviewService.getApprovedReviews());
    }

    /**
     * GET /api/reviews/{id}
     * Get a single review by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Review not found with id: " + id
            )));
    }

    /**
     * POST /api/reviews
     * Submit a customer review.
     * Reviews start as unapproved until admin approves them.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitReview(@Valid @RequestBody Review review) {
        Review saved = reviewService.submitReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "message", "Thank you " + saved.getCustomerName() + " for your review! It will appear after approval.",
            "id", saved.getId()
        ));
    }

    /**
     * PUT /api/reviews/{id}/approve
     * Approve a review so it appears on the website (admin).
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveReview(@PathVariable Long id) {
        return reviewService.approveReview(id)
            .<ResponseEntity<?>>map(review -> ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Review approved and is now visible on the website."
            )))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Review not found with id: " + id
            )));
    }

    /**
     * PUT /api/reviews/{id}/reject
     * Reject/hide a review (admin).
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectReview(@PathVariable Long id) {
        return reviewService.rejectReview(id)
            .<ResponseEntity<?>>map(review -> ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Review rejected and hidden from the website."
            )))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Review not found with id: " + id
            )));
    }
}
