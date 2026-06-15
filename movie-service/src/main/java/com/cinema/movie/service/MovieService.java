package com.cinema.movie.service;

import com.cinema.movie.dto.*;
import com.cinema.movie.exception.MovieAlreadyExistsException;
import com.cinema.movie.exception.MovieNotFoundException;
import com.cinema.movie.exception.ScreeningNotFoundException;
import com.cinema.movie.model.Movie;
import com.cinema.movie.model.Screening;
import com.cinema.movie.repository.MovieRepository;
import com.cinema.movie.repository.ScreeningRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;

    public MovieService(MovieRepository movieRepository,
                        ScreeningRepository screeningRepository) {
        this.movieRepository = movieRepository;
        this.screeningRepository = screeningRepository;
    }

    public MovieResponse createMovie(MovieRequest request) {
    	if (movieRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new MovieAlreadyExistsException("Movie already exists with title: " + request.getTitle());
        }
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setDurationMin(request.getDurationMin());
        movie.setGenre(request.getGenre());
        Movie saved = movieRepository.save(movie);
        return mapToMovieResponse(saved);
    }

    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::mapToMovieResponse)
                .collect(Collectors.toList());
    }

    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        return mapToMovieResponse(movie);
    }

    public List<MovieResponse> searchMovies(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::mapToMovieResponse)
                .collect(Collectors.toList());
    }

    public List<MovieResponse> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre).stream()
                .map(this::mapToMovieResponse)
                .collect(Collectors.toList());
    }

    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }

    public ScreeningResponse createScreening(ScreeningRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + request.getMovieId()));
        Screening screening = new Screening();
        screening.setMovie(movie);
        screening.setHallId(request.getHallId());
        screening.setStartTime(request.getStartTime());
        screening.setEndTime(request.getEndTime());
        screening.setDate(request.getDate());
        Screening saved = screeningRepository.save(screening);
        return mapToScreeningResponse(saved);
    }

    public List<ScreeningResponse> getAllScreenings() {
        return screeningRepository.findAll().stream()
                .map(this::mapToScreeningResponse)
                .collect(Collectors.toList());
    }

    public List<ScreeningResponse> getScreeningsByMovie(Long movieId) {
        return screeningRepository.findByMovieId(movieId).stream()
                .map(this::mapToScreeningResponse)
                .collect(Collectors.toList());
    }

    public List<ScreeningResponse> getScreeningsByDate(LocalDate date) {
        return screeningRepository.findByDate(date).stream()
                .map(this::mapToScreeningResponse)
                .collect(Collectors.toList());
    }

    public ScreeningResponse getScreeningById(Long id) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new ScreeningNotFoundException("Screening not found with id: " + id));
        return mapToScreeningResponse(screening);
    }

    public void deleteScreening(Long id) {
        if (!screeningRepository.existsById(id)) {
            throw new ScreeningNotFoundException("Screening not found with id: " + id);
        }
        screeningRepository.deleteById(id);
    }

    private MovieResponse mapToMovieResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDurationMin(),
                movie.getGenre()
        );
    }

    private ScreeningResponse mapToScreeningResponse(Screening screening) {
        return new ScreeningResponse(
                screening.getId(),
                screening.getMovie().getId(),
                screening.getMovie().getTitle(),
                screening.getHallId(),
                screening.getStartTime(),
                screening.getEndTime(),
                screening.getDate()
        );
    }
}