package com.thedevlair.user.exception.type;


public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message, Object... args) {
        super(String.format(message, args));
    }
}
