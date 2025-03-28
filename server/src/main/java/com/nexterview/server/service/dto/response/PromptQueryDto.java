package com.nexterview.server.service.dto.response;

import com.nexterview.server.domain.PromptQuery;

public record PromptQueryDto(
        Long id,
        String query
) {

    public static PromptQueryDto of(PromptQuery promptQuery) {
        return new PromptQueryDto(promptQuery.getId(), promptQuery.getQuery());
    }
}
