package com.nexterview.server.domain.validator;

public class StringValidator {

    private static final String STRING_EMPTY = "빈 문자열은 입력할 수 없습니다.";
    private static final String LENGTH_INVALID = "입력 문자열은 %d자 이상 %d자 이하여야 합니다.";

    private final Range lengthRange;
    private final boolean canBeEmpty;

    public StringValidator(Range lengthRange, boolean canBeEmpty) {
        this.lengthRange = lengthRange;
        this.canBeEmpty = canBeEmpty;
    }

    public static StringValidator notEmptyWithRange(int minLength, int maxLength) {
        Range range = new Range(minLength, maxLength);
        return new StringValidator(range, false);
    }

    public static StringValidator emptyWithRange(int minLength, int maxLength) {
        Range range = new Range(minLength, maxLength);
        return new StringValidator(range, true);
    }

    public void validate(String input) {
        boolean isInputEmpty = input == null || input.isBlank();
        if (isInputEmpty && !canBeEmpty) {
            throw new StringInvalidException(STRING_EMPTY);
        }
        if (!isInputEmpty && lengthRange.outOfRange(input.length())) {
            throw new StringInvalidException(LENGTH_INVALID, lengthRange.min(), lengthRange.max());
        }
    }
}
