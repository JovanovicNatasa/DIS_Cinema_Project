package com.cinema.movie.repository;

import com.cinema.movie.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {
    List<Screening> findByMovieId(Long movieId);
    List<Screening> findByHallId(Long hallId);
    List<Screening> findByDate(LocalDate date);
    List<Screening> findByMovieIdAndDate(Long movieId, LocalDate date);
}