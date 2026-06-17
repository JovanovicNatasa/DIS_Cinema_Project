package com.cinema.movie;

import com.cinema.movie.dto.*;
import com.cinema.movie.model.Movie;
import com.cinema.movie.model.Screening;
import com.cinema.movie.repository.MovieRepository;
import com.cinema.movie.repository.ScreeningRepository;
import com.cinema.movie.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ScreeningRepository screeningRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setDurationMin(148);
        movie.setGenre("Sci-Fi");
    }

    @Test
    void createMovie_ShouldReturnMovieResponse() {
        MovieRequest request = new MovieRequest();
        request.setTitle("Inception");
        request.setDurationMin(148);
        request.setGenre("Sci-Fi");

        when(movieRepository.existsByTitleIgnoreCase("Inception")).thenReturn(false);
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieResponse response = movieService.createMovie(request);

        assertNotNull(response);
        assertEquals("Inception", response.getTitle());
        assertEquals("Sci-Fi", response.getGenre());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void createMovie_ShouldThrowException_WhenMovieAlreadyExists() {
        MovieRequest request = new MovieRequest();
        request.setTitle("Inception");
        request.setDurationMin(148);
        request.setGenre("Sci-Fi");

        when(movieRepository.existsByTitleIgnoreCase("Inception")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> movieService.createMovie(request));

        assertEquals("Movie already exists with title: Inception", exception.getMessage());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void getMovieById_ShouldReturnMovie_WhenExists() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        MovieResponse response = movieService.getMovieById(1L);

        assertNotNull(response);
        assertEquals("Inception", response.getTitle());
    }

    @Test
    void getMovieById_ShouldThrowException_WhenNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> movieService.getMovieById(99L));

        assertEquals("Movie not found with id: 99", exception.getMessage());
    }

    @Test
    void createScreening_ShouldReturnScreeningResponse() {
        ScreeningRequest request = new ScreeningRequest();
        request.setMovieId(1L);
        request.setHallId(1L);
        request.setStartTime(LocalTime.of(18, 0));
        request.setEndTime(LocalTime.of(20, 28));
        request.setDate(LocalDate.of(2026, 6, 1));

        Screening screening = new Screening();
        screening.setId(1L);
        screening.setMovie(movie);
        screening.setHallId(1L);
        screening.setStartTime(LocalTime.of(18, 0));
        screening.setEndTime(LocalTime.of(20, 28));
        screening.setDate(LocalDate.of(2026, 6, 1));

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(screeningRepository.save(any(Screening.class))).thenReturn(screening);

        ScreeningResponse response = movieService.createScreening(request);

        assertNotNull(response);
        assertEquals(1L, response.getMovieId());
        assertEquals("Inception", response.getMovieTitle());
    }

    @Test
    void getAllMovies_ShouldReturnList() {
        when(movieRepository.findAll()).thenReturn(List.of(movie));

        List<MovieResponse> result = movieService.getAllMovies();

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }
    @Test
    void deleteMovie_ShouldThrowException_WhenNotFound() {
        when(movieRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> movieService.deleteMovie(99L));

        assertEquals("Movie not found with id: 99", exception.getMessage());
        verify(movieRepository, never()).deleteById(any());
    }

    @Test
    void getScreeningById_ShouldThrowException_WhenNotFound() {
        when(screeningRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> movieService.getScreeningById(99L));

        assertEquals("Screening not found with id: 99", exception.getMessage());
    }
}