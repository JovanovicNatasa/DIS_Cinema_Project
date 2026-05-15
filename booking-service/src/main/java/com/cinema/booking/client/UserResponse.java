package com.cinema.booking.client;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String firstName;
    private String email;
    private String roleName;
}