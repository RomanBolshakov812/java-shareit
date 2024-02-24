package ru.practicum.exception;

public class NullObjectException extends RuntimeException {
    public NullObjectException(final String message) {
        super(message);
    }
}
