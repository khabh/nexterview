package com.nexterview.server.exception;

public class NexterviewException extends RuntimeException {

    private final NexterviewErrorCode errorCode;

    public NexterviewException(NexterviewErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public NexterviewException(NexterviewErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
    }
}
