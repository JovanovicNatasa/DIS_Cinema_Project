package com.cinema.movie.exception;

public class ScreeningNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ScreeningNotFoundException(String message) {
        super(message);
    }
}