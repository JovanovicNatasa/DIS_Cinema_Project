package com.cinema.cinema.exception;

public class HallNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public HallNotFoundException(String message) {
        super(message);
    }
}