package com.nexterview.server.service;

import com.nexterview.server.domain.CustomizedPrompt;
import com.nexterview.server.service.dto.response.GeneratedDialogueDto;
import java.util.List;

public interface DialogueGenerator {

    List<GeneratedDialogueDto> generate(CustomizedPrompt customizedPrompt);
}
