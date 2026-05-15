package com.cinema.booking.dto;

import com.cinema.booking.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long id;
    private Long userId;
    private Long screeningId;
    private Long seatId;
    private BookingStatus status;
    private LocalDateTime createdAt;
}