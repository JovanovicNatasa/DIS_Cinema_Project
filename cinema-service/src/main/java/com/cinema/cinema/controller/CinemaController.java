package com.cinema.cinema.controller;

import com.cinema.cinema.dto.*;
import com.cinema.cinema.service.CinemaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
public class CinemaController {

    private final CinemaService cinemaService;

    public CinemaController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @PostMapping
    public ResponseEntity<CinemaResponse> createCinema(@Valid @RequestBody CinemaRequest request) {
        return ResponseEntity.ok(cinemaService.createCinema(request));
    }

    @GetMapping
    public ResponseEntity<List<CinemaResponse>> getAllCinemas() {
        return ResponseEntity.ok(cinemaService.getAllCinemas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaResponse> getCinemaById(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaService.getCinemaById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCinema(@PathVariable Long id) {
        cinemaService.deleteCinema(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/halls")
    public ResponseEntity<HallResponse> createHall(@Valid @RequestBody HallRequest request) {
        return ResponseEntity.ok(cinemaService.createHall(request));
    }

    @GetMapping("/{cinemaId}/halls")
    public ResponseEntity<List<HallResponse>> getHallsByCinema(@PathVariable Long cinemaId) {
        return ResponseEntity.ok(cinemaService.getHallsByCinema(cinemaId));
    }

    @GetMapping("/halls/{hallId}/seats")
    public ResponseEntity<List<SeatResponse>> getSeatsByHall(@PathVariable Long hallId) {
        return ResponseEntity.ok(cinemaService.getSeatsByHall(hallId));
    }

    @GetMapping("/halls/{hallId}/seats/available")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(@PathVariable Long hallId) {
        return ResponseEntity.ok(cinemaService.getAvailableSeatsByHall(hallId));
    }

    @PutMapping("/seats/{seatId}/availability")
    public ResponseEntity<Void> updateSeatAvailability(@PathVariable Long seatId,
                                                        @RequestParam Boolean isAvailable) {
        cinemaService.updateSeatAvailability(seatId, isAvailable);
        return ResponseEntity.ok().build();
    }
}