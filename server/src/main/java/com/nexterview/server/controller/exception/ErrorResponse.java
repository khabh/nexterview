package com.nexterview.server.controller.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
