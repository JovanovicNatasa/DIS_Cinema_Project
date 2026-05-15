package com.cinema.notification.service;

import com.cinema.notification.model.Notification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public Notification sendBookingConfirmation(String toEmail, String movieTitle,
                                                 String date, String time, String seatInfo) {
        String subject = "Booking Confirmation - " + movieTitle;
        String content = buildBookingConfirmationEmail(movieTitle, date, time, seatInfo);

        Notification notification = new Notification();
        notification.setEmail(toEmail);
        notification.setSubject(subject);
        notification.setContent(content);
        notification.setSentAt(LocalDateTime.now());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            notification.setSuccess(true);
        } catch (Exception e) {
            notification.setSuccess(false);
            System.err.println("Failed to send email to: " + toEmail + " - " + e.getMessage());
        }

        return notification;
    }

    public Notification sendCancellationConfirmation(String toEmail, String movieTitle,
                                                      String date, String time) {
        String subject = "Booking Cancellation - " + movieTitle;
        String content = buildCancellationEmail(movieTitle, date, time);

        Notification notification = new Notification();
        notification.setEmail(toEmail);
        notification.setSubject(subject);
        notification.setContent(content);
        notification.setSentAt(LocalDateTime.now());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            notification.setSuccess(true);
        } catch (Exception e) {
            notification.setSuccess(false);
            System.err.println("Failed to send email to: " + toEmail + " - " + e.getMessage());
        }

        return notification;
    }

    private String buildBookingConfirmationEmail(String movieTitle, String date,
                                                   String time, String seatInfo) {
        return String.format(
            "Dear Customer,\n\n" +
            "Your booking has been confirmed!\n\n" +
            "Movie: %s\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Seat: %s\n\n" +
            "Thank you for choosing our cinema!\n\n" +
            "Best regards,\nCinema Team",
            movieTitle, date, time, seatInfo
        );
    }

    private String buildCancellationEmail(String movieTitle, String date, String time) {
        return String.format(
            "Dear Customer,\n\n" +
            "Your booking has been cancelled.\n\n" +
            "Movie: %s\n" +
            "Date: %s\n" +
            "Time: %s\n\n" +
            "We hope to see you again soon!\n\n" +
            "Best regards,\nCinema Team",
            movieTitle, date, time
        );
    }
}