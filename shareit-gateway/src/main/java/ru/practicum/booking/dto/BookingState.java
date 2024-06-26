package ru.practicum.booking.dto;

import java.util.Optional;

public enum BookingState {
    ALL,
    FUTURE,
    CURRENT,
    PAST,
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;
    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
            return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
