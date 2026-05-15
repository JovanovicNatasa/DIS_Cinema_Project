package com.cinema.notification.listener;

import com.cinema.notification.config.RabbitMQConfig;
import com.cinema.notification.dto.NotificationMessage;
import com.cinema.notification.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private final EmailService emailService;

    public NotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(NotificationMessage message) {
        System.out.println("Received notification message for booking: " + message.getBookingId());

        if ("BOOKING_CONFIRMED".equals(message.getMessageType())) {
            emailService.sendBookingConfirmation(
                    message.getUserEmail(),
                    message.getMovieTitle(),
                    message.getScreeningDate(),
                    message.getScreeningTime(),
                    message.getSeatInfo()
            );
        } else if ("BOOKING_CANCELLED".equals(message.getMessageType())) {
            emailService.sendCancellationConfirmation(
                    message.getUserEmail(),
                    message.getMovieTitle(),
                    message.getScreeningDate(),
                    message.getScreeningTime()
            );
        }
    }
}