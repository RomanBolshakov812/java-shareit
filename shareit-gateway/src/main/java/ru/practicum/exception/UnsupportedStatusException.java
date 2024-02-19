package ru.practicum.exception;

public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException(final String message) {
        super(message);
    }
}
