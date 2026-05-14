package com.cinema.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemaResponse {
    private Long id;
    private String name;
    private String location;
}