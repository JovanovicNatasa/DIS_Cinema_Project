package com.cinema.cinema;

import com.cinema.cinema.dto.CinemaRequest;
import com.cinema.cinema.dto.CinemaResponse;
import com.cinema.cinema.dto.HallRequest;
import com.cinema.cinema.dto.HallResponse;
import com.cinema.cinema.repository.CinemaRepository;
import com.cinema.cinema.repository.HallRepository;
import com.cinema.cinema.repository.SeatRepository;
import com.cinema.cinema.service.CinemaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class CinemaServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("cinemadb_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private SeatRepository seatRepository;

    @BeforeEach
    void setUp() {
        seatRepository.deleteAll();
        hallRepository.deleteAll();
        cinemaRepository.deleteAll();
    }

    @Test
    void createCinema_ShouldPersistInRealDatabase() {
        CinemaRequest request = new CinemaRequest();
        request.setName("Cinestar");
        request.setLocation("Belgrade");

        CinemaResponse response = cinemaService.createCinema(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(1, cinemaRepository.count());
    }

    @Test
    void getCinemaById_ShouldThrowException_WhenNotFoundInDatabase() {
        assertThrows(RuntimeException.class, () -> cinemaService.getCinemaById(999L));
    }

    @Test
    void createHall_ShouldAutoGenerateSeatsInRealDatabase() {
        CinemaRequest cinemaRequest = new CinemaRequest();
        cinemaRequest.setName("Cinestar");
        cinemaRequest.setLocation("Belgrade");
        CinemaResponse cinema = cinemaService.createCinema(cinemaRequest);

        HallRequest hallRequest = new HallRequest();
        hallRequest.setCinemaId(cinema.getId());
        hallRequest.setName("Hall A");
        hallRequest.setCapacity(80);

        HallResponse hall = cinemaService.createHall(hallRequest);

        assertNotNull(hall);
        assertEquals(80, hall.getCapacity());
        assertEquals(80, seatRepository.findByHallId(hall.getId()).size());
    }

    @Test
    void getHallsByCinema_ShouldThrowException_WhenCinemaNotFoundInDatabase() {
        assertThrows(RuntimeException.class, () -> cinemaService.getHallsByCinema(999L));
    }
}