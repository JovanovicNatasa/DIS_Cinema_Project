package com.cinema.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatResponse {
    private Long id;
    private Long hallId;
    private String rowNumber;
    private Integer seatNumber;
    private Boolean isAvailable;
}