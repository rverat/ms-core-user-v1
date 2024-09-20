package com.thedevlair.user.exception.type;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message, Object... args) {
        super(String.format(message, args));
    }
}
