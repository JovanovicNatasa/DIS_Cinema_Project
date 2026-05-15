package com.cinema.booking.repository;

import com.cinema.booking.model.Booking;
import com.cinema.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByScreeningId(Long screeningId);
    List<Booking> findByStatus(BookingStatus status);
}