package com.cinema.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponse {
    private Long id;
    private String title;
    private Integer durationMin;
    private String genre;
}