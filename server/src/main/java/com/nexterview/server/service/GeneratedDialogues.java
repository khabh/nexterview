package com.nexterview.server.service;

import com.nexterview.server.service.dto.response.GeneratedDialogueDto;
import java.util.List;

public record GeneratedDialogues(
        Integer totalTokens,
        List<GeneratedDialogueDto> dialogues
) {
}
