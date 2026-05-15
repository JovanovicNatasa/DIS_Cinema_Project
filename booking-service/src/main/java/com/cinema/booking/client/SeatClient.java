package com.cinema.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cinema-service", fallback = SeatClientFallback.class)
public interface SeatClient {

    @PutMapping("/api/cinemas/seats/{seatId}/availability")
    void updateSeatAvailability(@PathVariable Long seatId, @RequestParam Boolean isAvailable);
}