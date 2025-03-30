package com.nexterview.server.service.dto.response;

import com.nexterview.server.domain.PromptAnswer;
import com.nexterview.server.domain.PromptQuery;

public record PromptAnswerDto(
        Long id,
        Long promptQueryId,
        String query,
        String answer
) {

    public static PromptAnswerDto of(PromptAnswer promptAnswer) {
        PromptQuery promptQuery = promptAnswer.getQuery();
        return new PromptAnswerDto(
                promptAnswer.getId(),
                promptQuery.getId(),
                promptQuery.getQuery(),
                promptAnswer.getAnswer()
        );
    }
}
