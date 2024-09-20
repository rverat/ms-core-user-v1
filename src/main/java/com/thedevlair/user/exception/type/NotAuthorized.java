package com.thedevlair.user.exception.type;

public class NotAuthorized extends RuntimeException {

    public NotAuthorized(String message, Object... args) {
        super(String.format(message, args));
    }
}
