package com.cinema.notification;

import com.cinema.notification.dto.NotificationMessage;
import com.cinema.notification.listener.NotificationListener;
import com.cinema.notification.model.Notification;
import com.cinema.notification.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationListener notificationListener;

    private NotificationMessage bookingConfirmedMessage;
    private NotificationMessage bookingCancelledMessage;
    private Notification successNotification;

    @BeforeEach
    void setUp() {
        bookingConfirmedMessage = new NotificationMessage(
                1L, 1L, "john@example.com",
                "Inception", "2026-06-01", "18:00",
                "Seat A1", "BOOKING_CONFIRMED"
        );

        bookingCancelledMessage = new NotificationMessage(
                1L, 1L, "john@example.com",
                "Inception", "2026-06-01", "18:00",
                "Seat A1", "BOOKING_CANCELLED"
        );

        successNotification = new Notification();
        successNotification.setEmail("john@example.com");
        successNotification.setSubject("Booking Confirmation - Inception");
        successNotification.setSuccess(true);
        successNotification.setSentAt(LocalDateTime.now());
    }

    @Test
    void handleNotification_ShouldSendConfirmationEmail_WhenBookingConfirmed() {
        when(emailService.sendBookingConfirmation(anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(successNotification);

        notificationListener.handleNotification(bookingConfirmedMessage);

        verify(emailService, times(1)).sendBookingConfirmation(
                "john@example.com", "Inception",
                "2026-06-01", "18:00", "Seat A1"
        );
        verify(emailService, never()).sendCancellationConfirmation(
                anyString(), anyString(), anyString(), anyString()
        );
    }

    @Test
    void handleNotification_ShouldSendCancellationEmail_WhenBookingCancelled() {
        when(emailService.sendCancellationConfirmation(anyString(), anyString(),
                anyString(), anyString())).thenReturn(successNotification);

        notificationListener.handleNotification(bookingCancelledMessage);

        verify(emailService, times(1)).sendCancellationConfirmation(
                "john@example.com", "Inception",
                "2026-06-01", "18:00"
        );
        verify(emailService, never()).sendBookingConfirmation(
                anyString(), anyString(), anyString(), anyString(), anyString()
        );
    }

    @Test
    void handleNotification_ShouldNotSendEmail_WhenUnknownMessageType() {
        NotificationMessage unknownMessage = new NotificationMessage(
                1L, 1L, "john@example.com",
                "Inception", "2026-06-01", "18:00",
                "Seat A1", "UNKNOWN_TYPE"
        );

        notificationListener.handleNotification(unknownMessage);

        verify(emailService, never()).sendBookingConfirmation(
                anyString(), anyString(), anyString(), anyString(), anyString()
        );
        verify(emailService, never()).sendCancellationConfirmation(
                anyString(), anyString(), anyString(), anyString()
        );
    }
}