package com.cinema.booking.client;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScreeningResponse {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long hallId;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
}