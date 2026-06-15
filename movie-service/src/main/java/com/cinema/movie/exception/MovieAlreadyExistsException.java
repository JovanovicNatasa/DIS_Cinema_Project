package com.cinema.movie.exception;

public class MovieAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MovieAlreadyExistsException(String message) {
        super(message);
    }
}