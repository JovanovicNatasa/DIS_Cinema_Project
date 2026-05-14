package com.cinema.cinema.repository;

import com.cinema.cinema.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    List<Cinema> findByNameContainingIgnoreCase(String name);
}