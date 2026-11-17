package com.auca.exceptions;

public class PrerequisiteMissingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PrerequisiteMissingException(String message) {
        super(message);
    }
}
