package com.cinema.cinema.service;

import com.cinema.cinema.dto.*;
import com.cinema.cinema.model.Cinema;
import com.cinema.cinema.model.Hall;
import com.cinema.cinema.model.Seat;
import com.cinema.cinema.repository.CinemaRepository;
import com.cinema.cinema.repository.HallRepository;
import com.cinema.cinema.repository.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    public CinemaService(CinemaRepository cinemaRepository,
                         HallRepository hallRepository,
                         SeatRepository seatRepository) {
        this.cinemaRepository = cinemaRepository;
        this.hallRepository = hallRepository;
        this.seatRepository = seatRepository;
    }

    public CinemaResponse createCinema(CinemaRequest request) {
        Cinema cinema = new Cinema();
        cinema.setName(request.getName());
        cinema.setLocation(request.getLocation());
        Cinema saved = cinemaRepository.save(cinema);
        return new CinemaResponse(saved.getId(), saved.getName(), saved.getLocation());
    }

    public List<CinemaResponse> getAllCinemas() {
        return cinemaRepository.findAll().stream()
                .map(c -> new CinemaResponse(c.getId(), c.getName(), c.getLocation()))
                .collect(Collectors.toList());
    }

    public CinemaResponse getCinemaById(Long id) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cinema not found"));
        return new CinemaResponse(cinema.getId(), cinema.getName(), cinema.getLocation());
    }

    public void deleteCinema(Long id) {
        cinemaRepository.deleteById(id);
    }

    public HallResponse createHall(HallRequest request) {
        Cinema cinema = cinemaRepository.findById(request.getCinemaId())
                .orElseThrow(() -> new RuntimeException("Cinema not found"));

        Hall hall = new Hall();
        hall.setCinema(cinema);
        hall.setName(request.getName());
        hall.setCapacity(request.getCapacity());
        hall.setAvailableSeats(request.getCapacity());
        Hall savedHall = hallRepository.save(hall);

        // Auto-generate seats
        List<Seat> seats = new ArrayList<>();
        String[] rows = {"A", "B", "C", "D", "E", "F", "G", "H"};
        int seatsPerRow = request.getCapacity() / rows.length;
        for (String row : rows) {
            for (int i = 1; i <= seatsPerRow; i++) {
                Seat seat = new Seat();
                seat.setHall(savedHall);
                seat.setRowNumber(row);
                seat.setSeatNumber(i);
                seat.setIsAvailable(true);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);

        return new HallResponse(savedHall.getId(), cinema.getId(),
                cinema.getName(), savedHall.getName(),
                savedHall.getCapacity(), savedHall.getAvailableSeats());
    }

    public List<HallResponse> getHallsByCinema(Long cinemaId) {
        return hallRepository.findByCinemaId(cinemaId).stream()
                .map(h -> new HallResponse(h.getId(), h.getCinema().getId(),
                        h.getCinema().getName(), h.getName(),
                        h.getCapacity(), h.getAvailableSeats()))
                .collect(Collectors.toList());
    }

    public List<SeatResponse> getSeatsByHall(Long hallId) {
        return seatRepository.findByHallId(hallId).stream()
                .map(s -> new SeatResponse(s.getId(), s.getHall().getId(),
                        s.getRowNumber(), s.getSeatNumber(), s.getIsAvailable()))
                .collect(Collectors.toList());
    }

    public List<SeatResponse> getAvailableSeatsByHall(Long hallId) {
        return seatRepository.findByHallIdAndIsAvailable(hallId, true).stream()
                .map(s -> new SeatResponse(s.getId(), s.getHall().getId(),
                        s.getRowNumber(), s.getSeatNumber(), s.getIsAvailable()))
                .collect(Collectors.toList());
    }

    public void updateSeatAvailability(Long seatId, Boolean isAvailable) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        seat.setIsAvailable(isAvailable);
        seatRepository.save(seat);
    }
}