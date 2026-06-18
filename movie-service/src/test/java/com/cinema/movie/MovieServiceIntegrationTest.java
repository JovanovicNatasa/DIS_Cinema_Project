package com.cinema.movie;

import com.cinema.movie.dto.MovieRequest;
import com.cinema.movie.dto.MovieResponse;
import com.cinema.movie.dto.ScreeningRequest;
import com.cinema.movie.dto.ScreeningResponse;
import com.cinema.movie.repository.MovieRepository;
import com.cinema.movie.repository.ScreeningRepository;
import com.cinema.movie.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class MovieServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("moviedb_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @BeforeEach
    void setUp() {
        screeningRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void createMovie_ShouldPersistInRealDatabase() {
        MovieRequest request = new MovieRequest();
        request.setTitle("Inception");
        request.setDurationMin(148);
        request.setGenre("Sci-Fi");

        MovieResponse response = movieService.createMovie(request);

        assertNotNull(response);
        assertEquals("Inception", response.getTitle());
        assertEquals(1, movieRepository.count());
    }

    @Test
    void createMovie_ShouldThrowException_WhenTitleAlreadyExistsInDatabase() {
        MovieRequest request = new MovieRequest();
        request.setTitle("Inception");
        request.setDurationMin(148);
        request.setGenre("Sci-Fi");
        movieService.createMovie(request);

        MovieRequest duplicateRequest = new MovieRequest();
        duplicateRequest.setTitle("Inception");
        duplicateRequest.setDurationMin(150);
        duplicateRequest.setGenre("Action");

        assertThrows(RuntimeException.class, () -> movieService.createMovie(duplicateRequest));
        assertEquals(1, movieRepository.count());
    }

    @Test
    void getMovieById_ShouldThrowException_WhenNotFoundInDatabase() {
        assertThrows(RuntimeException.class, () -> movieService.getMovieById(999L));
    }

    @Test
    void createScreening_ShouldPersistInRealDatabase() {
        MovieRequest movieRequest = new MovieRequest();
        movieRequest.setTitle("Inception");
        movieRequest.setDurationMin(148);
        movieRequest.setGenre("Sci-Fi");
        MovieResponse movie = movieService.createMovie(movieRequest);

        ScreeningRequest screeningRequest = new ScreeningRequest();
        screeningRequest.setMovieId(movie.getId());
        screeningRequest.setHallId(1L);
        screeningRequest.setStartTime(LocalTime.of(18, 0));
        screeningRequest.setEndTime(LocalTime.of(20, 28));
        screeningRequest.setDate(LocalDate.of(2026, 6, 1));

        ScreeningResponse screening = movieService.createScreening(screeningRequest);

        assertNotNull(screening);
        assertEquals("Inception", screening.getMovieTitle());
        assertEquals(1, screeningRepository.count());
    }

    @Test
    void getScreeningById_ShouldThrowException_WhenNotFoundInDatabase() {
        assertThrows(RuntimeException.class, () -> movieService.getScreeningById(999L));
    }
}