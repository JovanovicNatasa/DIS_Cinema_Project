package com.cinema.movie.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScreeningRequest {
    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Hall ID is required")
    private Long hallId;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Date is required")
    private LocalDate date;
}