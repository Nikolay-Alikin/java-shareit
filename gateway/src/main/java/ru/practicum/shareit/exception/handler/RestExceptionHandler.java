package ru.practicum.shareit.exception.handler;

import ru.practicum.shareit.exception.Error;
import ru.practicum.shareit.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServerException.class)
    public ResponseEntity<Error> handleServerException(final ServerException exception) {
        return buildResponse(exception);
    }

    private ResponseEntity<Error> buildResponse(ServerException exception) {
        return switch (exception.getErrorCode()) {
            case 400 -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Error(exception.getClass().getName(), exception.getMessage()));
            case 404 -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Error(exception.getClass().getName(), exception.getMessage()));
            case 409 -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Error(exception.getClass().getName(), exception.getMessage()));
            case 500 -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Error(exception.getClass().getName(), exception.getMessage()));
            default -> ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
                    .body(new Error(exception.getClass().getName(), exception.getMessage()));
        };
    }
}

