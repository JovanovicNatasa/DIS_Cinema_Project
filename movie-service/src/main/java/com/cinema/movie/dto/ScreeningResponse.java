package com.cinema.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreeningResponse {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long hallId;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
}