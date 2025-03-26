package com.nexterview.server.domain.validator;

public record Range(
        int min,
        int max
) {

    public boolean outOfRange(int value) {
        return value < min || value > max;
    }
}
