package com.cinema.booking.service;

import com.cinema.booking.client.ScreeningClient;
import com.cinema.booking.client.ScreeningResponse;
import com.cinema.booking.client.SeatClient;
import com.cinema.booking.client.UserClient;
import com.cinema.booking.client.UserResponse;
import com.cinema.booking.config.RabbitMQConfig;
import com.cinema.booking.dto.BookingRequest;
import com.cinema.booking.dto.BookingResponse;
import com.cinema.booking.dto.NotificationMessage;
import com.cinema.booking.model.Booking;
import com.cinema.booking.model.BookingStatus;
import com.cinema.booking.repository.BookingRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserClient userClient;
    private final ScreeningClient screeningClient;
    private final SeatClient seatClient;
    private final RabbitTemplate rabbitTemplate;

    public BookingService(BookingRepository bookingRepository,
                          UserClient userClient,
                          ScreeningClient screeningClient,
                          SeatClient seatClient,
                          RabbitTemplate rabbitTemplate) {
        this.bookingRepository = bookingRepository;
        this.userClient = userClient;
        this.screeningClient = screeningClient;
        this.seatClient = seatClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public BookingResponse createBooking(BookingRequest request) {
        // Sync call to user-service
        UserResponse user = userClient.getUserById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Sync call to movie-service
        ScreeningResponse screening = screeningClient.getScreeningById(request.getScreeningId());
        if (screening == null) {
            throw new RuntimeException("Screening not found");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setScreeningId(request.getScreeningId());
        booking.setSeatId(request.getSeatId());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);

        // Sync call to cinema-service — mark seat as unavailable
        seatClient.updateSeatAvailability(request.getSeatId(), false);

        // Async — send notification via RabbitMQ
        NotificationMessage message = new NotificationMessage(
                saved.getId(),
                user.getId(),
                user.getEmail(),
                screening.getMovieTitle(),
                screening.getDate().toString(),
                screening.getStartTime().toString(),
                "Seat " + request.getSeatId(),
                "BOOKING_CONFIRMED"
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                message
        );

        return mapToResponse(saved);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToResponse(booking);
    }

    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        // Mark seat as available again
        seatClient.updateSeatAvailability(booking.getSeatId(), true);

        return mapToResponse(saved);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getScreeningId(),
                booking.getSeatId(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}