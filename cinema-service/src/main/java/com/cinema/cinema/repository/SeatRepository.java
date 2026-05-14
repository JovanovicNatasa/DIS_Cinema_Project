package com.cinema.cinema.repository;

import com.cinema.cinema.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByHallId(Long hallId);
    List<Seat> findByHallIdAndIsAvailable(Long hallId, Boolean isAvailable);
}