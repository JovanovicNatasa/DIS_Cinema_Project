package com.cinema.booking.client;

import org.springframework.stereotype.Component;

@Component
public class SeatClientFallback implements SeatClient {

    @Override
    public void updateSeatAvailability(Long seatId, Boolean isAvailable) {
        // fallback - log error
        System.err.println("Cinema service unavailable, seat availability not updated for seat: " + seatId);
    }
}