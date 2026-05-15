package com.cinema.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {
    private Long bookingId;
    private Long userId;
    private String userEmail;
    private String movieTitle;
    private String screeningDate;
    private String screeningTime;
    private String seatInfo;
    private String messageType;
}