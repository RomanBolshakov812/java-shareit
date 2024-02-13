package ru.practicum.exception;

import javax.persistence.EntityNotFoundException;

public class EntityNullException extends EntityNotFoundException {
    public EntityNullException(final String message) {
        super(message);
    }
}
