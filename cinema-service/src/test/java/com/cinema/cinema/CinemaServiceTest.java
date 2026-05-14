package com.cinema.cinema;

import com.cinema.cinema.dto.CinemaRequest;
import com.cinema.cinema.dto.CinemaResponse;
import com.cinema.cinema.dto.HallRequest;
import com.cinema.cinema.dto.HallResponse;
import com.cinema.cinema.model.Cinema;
import com.cinema.cinema.model.Hall;
import com.cinema.cinema.repository.CinemaRepository;
import com.cinema.cinema.repository.HallRepository;
import com.cinema.cinema.repository.SeatRepository;
import com.cinema.cinema.service.CinemaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CinemaServiceTest {

    @Mock
    private CinemaRepository cinemaRepository;
    @Mock
    private HallRepository hallRepository;
    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private CinemaService cinemaService;

    private Cinema cinema;

    @BeforeEach
    void setUp() {
        cinema = new Cinema();
        cinema.setId(1L);
        cinema.setName("Cinestar");
        cinema.setLocation("Belgrade");
    }

    @Test
    void createCinema_ShouldReturnCinemaResponse() {
        CinemaRequest request = new CinemaRequest();
        request.setName("Cinestar");
        request.setLocation("Belgrade");

        when(cinemaRepository.save(any(Cinema.class))).thenReturn(cinema);

        CinemaResponse response = cinemaService.createCinema(request);

        assertNotNull(response);
        assertEquals("Cinestar", response.getName());
        assertEquals("Belgrade", response.getLocation());
        verify(cinemaRepository, times(1)).save(any(Cinema.class));
    }

    @Test
    void getCinemaById_ShouldReturnCinema_WhenExists() {
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));

        CinemaResponse response = cinemaService.getCinemaById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Cinestar", response.getName());
    }

    @Test
    void getCinemaById_ShouldThrowException_WhenNotFound() {
        when(cinemaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cinemaService.getCinemaById(99L));

        assertEquals("Cinema not found", exception.getMessage());
    }

    @Test
    void createHall_ShouldCreateHallAndSeats() {
        HallRequest request = new HallRequest();
        request.setCinemaId(1L);
        request.setName("Hall A");
        request.setCapacity(80);

        Hall hall = new Hall();
        hall.setId(1L);
        hall.setCinema(cinema);
        hall.setName("Hall A");
        hall.setCapacity(80);
        hall.setAvailableSeats(80);

        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));
        when(hallRepository.save(any(Hall.class))).thenReturn(hall);

        HallResponse response = cinemaService.createHall(request);

        assertNotNull(response);
        assertEquals("Hall A", response.getName());
        assertEquals(80, response.getCapacity());
        verify(seatRepository, times(1)).saveAll(any());
    }

    @Test
    void getAllCinemas_ShouldReturnList() {
        when(cinemaRepository.findAll()).thenReturn(List.of(cinema));

        List<CinemaResponse> result = cinemaService.getAllCinemas();

        assertEquals(1, result.size());
        assertEquals("Cinestar", result.get(0).getName());
    }
}