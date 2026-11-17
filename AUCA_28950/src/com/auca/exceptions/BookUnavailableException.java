package com.auca.exceptions;

public class BookUnavailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BookUnavailableException(String message) {
        super(message);
    }
}
