package com.cinema.booking;

import com.cinema.booking.client.ScreeningClient;
import com.cinema.booking.client.ScreeningResponse;
import com.cinema.booking.client.UserClient;
import com.cinema.booking.client.UserResponse;
import com.cinema.booking.client.SeatClient;
import com.cinema.booking.dto.BookingRequest;
import com.cinema.booking.dto.BookingResponse;
import com.cinema.booking.model.Booking;
import com.cinema.booking.model.BookingStatus;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserClient userClient;
    @Mock
    private ScreeningClient screeningClient;
    @Mock
    private SeatClient seatClient;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private BookingService bookingService;

    private UserResponse userResponse;
    private ScreeningResponse screeningResponse;
    private Booking booking;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setFirstName("John");
        userResponse.setEmail("john@example.com");
        userResponse.setRoleName("USER");

        screeningResponse = new ScreeningResponse();
        screeningResponse.setId(1L);
        screeningResponse.setMovieId(1L);
        screeningResponse.setMovieTitle("Inception");
        screeningResponse.setHallId(1L);
        screeningResponse.setStartTime(LocalTime.of(18, 0));
        screeningResponse.setEndTime(LocalTime.of(20, 28));
        screeningResponse.setDate(LocalDate.of(2026, 6, 1));

        booking = new Booking();
        booking.setId(1L);
        booking.setUserId(1L);
        booking.setScreeningId(1L);
        booking.setSeatId(1L);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createBooking_ShouldReturnBookingResponse_WhenValid() {
        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setScreeningId(1L);
        request.setSeatId(1L);

        when(userClient.getUserById(1L)).thenReturn(userResponse);
        when(screeningClient.getScreeningById(1L)).thenReturn(screeningResponse);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        assertEquals(1L, response.getUserId());
        verify(seatClient, times(1)).updateSeatAvailability(1L, false);
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    void createBooking_ShouldThrowException_WhenUserNotFound() {
        BookingRequest request = new BookingRequest();
        request.setUserId(99L);
        request.setScreeningId(1L);
        request.setSeatId(1L);

        when(userClient.getUserById(99L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(request));

        assertEquals("User not found with id: 99", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancelBooking_ShouldUpdateStatusAndFreeSeat() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.cancelBooking(1L);

        verify(seatClient, times(1)).updateSeatAvailability(1L, true);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void getBookingsByUser_ShouldReturnList() {
        when(bookingRepository.findByUserId(1L)).thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getBookingsByUser(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }
    
    @Test
    void createBooking_ShouldThrowException_WhenScreeningNotFound() {
        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setScreeningId(99L);
        request.setSeatId(1L);

        when(userClient.getUserById(1L)).thenReturn(userResponse);
        when(screeningClient.getScreeningById(99L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(request));

        assertEquals("Screening not found with id: 99", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }
    
    @Test
    void getBookingById_ShouldThrowException_WhenNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.getBookingById(99L));

        assertEquals("Booking not found with id: 99", exception.getMessage());
    }
    
    @Test
    void cancelBooking_ShouldThrowException_WhenNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.cancelBooking(99L));

        assertEquals("Booking not found with id: 99", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }
}