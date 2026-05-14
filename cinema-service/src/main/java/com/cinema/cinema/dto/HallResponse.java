package com.cinema.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HallResponse {
    private Long id;
    private Long cinemaId;
    private String cinemaName;
    private String name;
    private Integer capacity;
    private Integer availableSeats;
}