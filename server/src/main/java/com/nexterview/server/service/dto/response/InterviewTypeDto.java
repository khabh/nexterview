package com.nexterview.server.service.dto.response;

import com.nexterview.server.domain.InterviewType;

public record InterviewTypeDto(
        String interviewType
) {

    public static InterviewTypeDto of(InterviewType interviewType) {
        return new InterviewTypeDto(interviewType.name());
    }
}
