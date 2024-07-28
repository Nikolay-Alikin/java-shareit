package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {

    private final int errorCode;

    public ServerException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
