package com.nexterview.server.service.dto.response;

import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptQuery;
import java.util.List;

public record PromptDto(
        Long id,
        String topic,
        String instruction,
        List<PromptQueryDto> queries
) {

    public static PromptDto of(Prompt prompt, List<PromptQuery> queries) {
        List<PromptQueryDto> promptQueries = queries.stream()
                .map(PromptQueryDto::of)
                .toList();

        return new PromptDto(prompt.getId(), prompt.getTopic(), prompt.getInstruction(), promptQueries);
    }
}
