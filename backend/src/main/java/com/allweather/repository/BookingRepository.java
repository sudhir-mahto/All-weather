package com.allweather.repository;

import com.allweather.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByOrderByCreatedAtDesc();

    List<Booking> findByStatus(Booking.BookingStatus status);

    List<Booking> findByPhoneOrderByCreatedAtDesc(String phone);
}
