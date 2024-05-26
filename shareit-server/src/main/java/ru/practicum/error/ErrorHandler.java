package ru.practicum.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NullObjectException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNullObjectException(final NullObjectException e) {
        return new ErrorResponse("Ошибка: объект не найден!", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse("Ошибка валидации!", e.getMessage());
    }

    @ExceptionHandler(EntityNullException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNullException(final EntityNullException e) {
        return new ErrorResponse("Ошибка: объект не найден!", e.getMessage());
    }
}
