package ru.practicum.shareit.exception;

public class NegativeValueException extends RuntimeException {
    public NegativeValueException(final String message) {
        super(message);
    }
}
