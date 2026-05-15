package com.cinema.booking.client;

import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserResponse getUserById(Long id) {
        return null;
    }
}