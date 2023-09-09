package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.NegativeValueException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NullObjectException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)// 500 ////////////////////////////////////////////////////
    public ErrorResponse handleNullObjectException(final NullObjectException e) {
        return new ErrorResponse("Ошибка: объект не найден!", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)// 400 /////////////////////////////////////////////////////
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse("Ошибка валидации!", e.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)// 409 //////////////////////////////////////////////////
    public ErrorResponse handleNullRequestException(final DuplicateException e) {
        return new ErrorResponse("Ошибка валидации!", e.getMessage());
    }

    @ExceptionHandler(NegativeValueException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNegativeValueException(final NegativeValueException e) {
        return new ErrorResponse("Ошибка: передано отрицательное значение!", e.getMessage());
    }
}
