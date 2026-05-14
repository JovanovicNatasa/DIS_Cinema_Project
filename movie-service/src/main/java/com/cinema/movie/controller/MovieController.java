package com.cinema.movie.controller;

import com.cinema.movie.dto.*;
import com.cinema.movie.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.createMovie(request));
    }

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchMovies(@RequestParam String title) {
        return ResponseEntity.ok(movieService.searchMovies(title));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieResponse>> getMoviesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(movieService.getMoviesByGenre(genre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/screenings")
    public ResponseEntity<ScreeningResponse> createScreening(@Valid @RequestBody ScreeningRequest request) {
        return ResponseEntity.ok(movieService.createScreening(request));
    }

    @GetMapping("/screenings")
    public ResponseEntity<List<ScreeningResponse>> getAllScreenings() {
        return ResponseEntity.ok(movieService.getAllScreenings());
    }

    @GetMapping("/{movieId}/screenings")
    public ResponseEntity<List<ScreeningResponse>> getScreeningsByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(movieService.getScreeningsByMovie(movieId));
    }

    @GetMapping("/screenings/date/{date}")
    public ResponseEntity<List<ScreeningResponse>> getScreeningsByDate(@PathVariable LocalDate date) {
        return ResponseEntity.ok(movieService.getScreeningsByDate(date));
    }

    @DeleteMapping("/screenings/{id}")
    public ResponseEntity<Void> deleteScreening(@PathVariable Long id) {
        movieService.deleteScreening(id);
        return ResponseEntity.noContent().build();
    }
}