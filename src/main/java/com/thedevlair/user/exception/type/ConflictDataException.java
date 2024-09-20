package com.thedevlair.user.exception.type;

public class ConflictDataException extends RuntimeException {
    public ConflictDataException(String message, Object... args) {
        super(String.format(message, args));
    }
}
