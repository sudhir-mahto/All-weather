package com.allweather.service;

import com.allweather.model.Booking;
import com.allweather.repository.BookingRepository;
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
public class BookingService {

    private final BookingRepository bookingRepository;

    /**
     * Create a new service booking.
     */
    public Booking createBooking(Booking booking) {
        booking.setStatus(Booking.BookingStatus.PENDING);
        Booking saved = bookingRepository.save(booking);
        log.info("New booking created: ID={}, Customer={}, Service={}, Phone={}",
                saved.getId(), saved.getCustomerName(), saved.getServiceType(), saved.getPhone());
        return saved;
    }

    /**
     * Get all bookings ordered by newest first.
     */
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get a single booking by ID.
     */
    @Transactional(readOnly = true)
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    /**
     * Update the status of a booking.
     */
    public Optional<Booking> updateBookingStatus(Long id, Booking.BookingStatus newStatus) {
        return bookingRepository.findById(id).map(booking -> {
            Booking.BookingStatus oldStatus = booking.getStatus();
            booking.setStatus(newStatus);
            Booking updated = bookingRepository.save(booking);
            log.info("Booking ID={} status updated: {} -> {}", id, oldStatus, newStatus);
            return updated;
        });
    }

    /**
     * Get bookings filtered by status.
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByStatus(Booking.BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }
}
