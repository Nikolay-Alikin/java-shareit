package ru.practicum.shareit.exception.handler;

import ru.practicum.shareit.exception.Error;
import ru.practicum.shareit.exception.BadRequestException;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Error handleConstraintViolationException(final ConstraintViolationException exception) {
        String message = StringUtils.substring(exception.getMessage(),
                StringUtils.indexOfAny(exception.getMessage(), "=") + 2,
                StringUtils.indexOf(exception.getMessage(), "propertyPath") - 3);
        return new Error(exception.getClass().getName(), message);
    }


    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public Error handleBadRequestException(final BadRequestException exception) {
        return new Error(exception.getClass().getName(), exception.getMessage());
    }
}

