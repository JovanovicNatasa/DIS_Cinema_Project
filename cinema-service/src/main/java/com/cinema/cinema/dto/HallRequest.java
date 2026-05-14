package com.cinema.cinema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HallRequest {
    @NotNull(message = "Cinema ID is required")
    private Long cinemaId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Capacity is required")
    private Integer capacity;
}