package com.nexterview.server.domain.validator;

public class StringInvalidException extends RuntimeException {

    public StringInvalidException(String message, Object... args) {
        super(String.format(message, args));
    }
}
