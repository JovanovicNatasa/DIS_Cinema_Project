package com.cinema.cinema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @Column(nullable = false)
    private String rowNumber;

    @Column(nullable = false)
    private Integer seatNumber;

    @Column(nullable = false)
    private Boolean isAvailable = true;
}