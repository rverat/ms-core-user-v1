package com.thedevlair.user.exception.type;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message, Object... args) {
        super(String.format(message, args));
    }
}
