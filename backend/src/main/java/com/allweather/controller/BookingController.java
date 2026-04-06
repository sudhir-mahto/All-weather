package com.allweather.controller;

import com.allweather.model.Booking;
import com.allweather.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for service booking management.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    /**
     * POST /api/bookings
     * Create a new service booking.
     * Called by the frontend booking form.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@Valid @RequestBody Booking booking) {
        Booking saved = bookingService.createBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "message", "Booking confirmed! We will contact you soon on " + saved.getPhone(),
            "bookingId", saved.getId(),
            "status", saved.getStatus().name()
        ));
    }

    /**
     * GET /api/bookings
     * Get all bookings. Intended for admin use.
     * Optional query param: ?status=PENDING|CONFIRMED|IN_PROGRESS|COMPLETED|CANCELLED
     */
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(
            @RequestParam(required = false) Booking.BookingStatus status) {
        if (status != null) {
            return ResponseEntity.ok(bookingService.getBookingsByStatus(status));
        }
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    /**
     * GET /api/bookings/{id}
     * Get a single booking by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Booking not found with id: " + id
            )));
    }

    /**
     * PUT /api/bookings/{id}/status
     * Update the status of a booking.
     * Request body: { "status": "CONFIRMED" }
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String statusStr = body.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Status field is required"
            ));
        }

        Booking.BookingStatus newStatus;
        try {
            newStatus = Booking.BookingStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid status. Valid values: PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED"
            ));
        }

        return bookingService.updateBookingStatus(id, newStatus)
            .<ResponseEntity<?>>map(updated -> ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Booking status updated to " + newStatus,
                "booking", updated
            )))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Booking not found with id: " + id
            )));
    }
}
