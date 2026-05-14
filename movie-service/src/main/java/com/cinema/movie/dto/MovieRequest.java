package com.cinema.movie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MovieRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Duration is required")
    private Integer durationMin;

    @NotBlank(message = "Genre is required")
    private String genre;
}