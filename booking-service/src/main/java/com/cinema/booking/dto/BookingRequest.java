package com.cinema.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Screening ID is required")
    private Long screeningId;

    @NotNull(message = "Seat ID is required")
    private Long seatId;
}