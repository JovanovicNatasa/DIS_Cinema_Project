package com.cinema.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "movie-service", fallback = ScreeningClientFallback.class)
public interface ScreeningClient {

    @GetMapping("/api/movies/screenings/{id}")
    ScreeningResponse getScreeningById(@PathVariable Long id);
}