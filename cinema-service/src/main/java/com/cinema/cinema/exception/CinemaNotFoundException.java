package com.cinema.cinema.exception;

public class CinemaNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CinemaNotFoundException(String message) {
        super(message);
    }
}