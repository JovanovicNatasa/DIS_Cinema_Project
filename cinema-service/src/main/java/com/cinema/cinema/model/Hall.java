package com.cinema.cinema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "halls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer availableSeats;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL)
    private List<Seat> seats;
}