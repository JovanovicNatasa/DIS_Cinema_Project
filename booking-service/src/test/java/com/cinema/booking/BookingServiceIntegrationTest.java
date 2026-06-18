package com.cinema.booking;

import com.cinema.booking.client.ScreeningClient;
import com.cinema.booking.client.ScreeningResponse;
import com.cinema.booking.client.SeatClient;
import com.cinema.booking.client.UserClient;
import com.cinema.booking.client.UserResponse;
import com.cinema.booking.dto.BookingRequest;
import com.cinema.booking.dto.BookingResponse;
import com.cinema.booking.model.BookingStatus;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
public class BookingServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("bookingdb_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    // External services are not available in the test environment (no Eureka, no RabbitMQ),
    // so Feign clients and RabbitTemplate are mocked. We are testing real persistence to the
    // booking database, not the external service communication itself (already covered by unit tests).
    @MockBean
    private UserClient userClient;

    @MockBean
    private ScreeningClient screeningClient;

    @MockBean
    private SeatClient seatClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    private UserResponse userResponse;
    private ScreeningResponse screeningResponse;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setFirstName("Ana");
        userResponse.setEmail("ana@cinema.com");
        userResponse.setRoleName("USER");

        screeningResponse = new ScreeningResponse();
        screeningResponse.setId(1L);
        screeningResponse.setMovieId(1L);
        screeningResponse.setMovieTitle("Inception");
        screeningResponse.setHallId(1L);
        screeningResponse.setStartTime(LocalTime.of(18, 0));
        screeningResponse.setEndTime(LocalTime.of(20, 28));
        screeningResponse.setDate(LocalDate.of(2026, 6, 1));
    }

    @Test
    void createBooking_ShouldPersistInRealDatabase() {
        when(userClient.getUserById(1L)).thenReturn(userResponse);
        when(screeningClient.getScreeningById(1L)).thenReturn(screeningResponse);

        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setScreeningId(1L);
        request.setSeatId(1L);

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        assertEquals(1, bookingRepository.count());
        verify(seatClient, times(1)).updateSeatAvailability(1L, false);
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    void cancelBooking_ShouldUpdateStatusInRealDatabase() {
        when(userClient.getUserById(1L)).thenReturn(userResponse);
        when(screeningClient.getScreeningById(1L)).thenReturn(screeningResponse);

        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setScreeningId(1L);
        request.setSeatId(1L);
        BookingResponse created = bookingService.createBooking(request);

        BookingResponse cancelled = bookingService.cancelBooking(created.getId());

        assertEquals(BookingStatus.CANCELLED, cancelled.getStatus());
        verify(seatClient, times(1)).updateSeatAvailability(1L, true);
    }

    @Test
    void getBookingById_ShouldThrowException_WhenNotFoundInDatabase() {
        assertThrows(RuntimeException.class, () -> bookingService.getBookingById(999L));
    }
}