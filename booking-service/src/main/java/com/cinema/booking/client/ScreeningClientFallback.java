package com.cinema.booking.client;

import org.springframework.stereotype.Component;

@Component
public class ScreeningClientFallback implements ScreeningClient {

    @Override
    public ScreeningResponse getScreeningById(Long id) {
        return null;
    }
}