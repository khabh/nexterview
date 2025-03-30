package com.nexterview.server.service.dto.response;

import com.nexterview.server.domain.Dialogue;

public record DialogueDto(
        Long id,
        String question,
        String answer
) {

    public static DialogueDto of(Dialogue dialogue) {
        return new DialogueDto(dialogue.getId(), dialogue.getQuestion(), dialogue.getAnswer());
    }
}
