package com.cinema.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private Boolean manageMovies;
    private Boolean promoteAdmin;
    private Boolean login;
    private Boolean register;
    private Boolean makeReservation;
    private Boolean browseMovies;
}