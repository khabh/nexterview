package com.nexterview.server.service;

import com.nexterview.server.domain.CustomizedPrompt;
import com.nexterview.server.domain.PromptComponent;
import com.nexterview.server.service.dto.response.GeneratedDialogueDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Primary
@Profile("local-dev")
public class SimulatedDialogueGenerator implements DialogueGenerator {

    public GeneratedDialogues generate(CustomizedPrompt customizedPrompt) {
        List<GeneratedDialogueDto> generatedDialogues = new ArrayList<>();
        generatedDialogues.add(new GeneratedDialogueDto("지시문은?", customizedPrompt.getInstruction()));
        for (PromptComponent promptComponent : customizedPrompt.getPromptComponents()) {
            generatedDialogues.add(new GeneratedDialogueDto("질문은: " + promptComponent.getQuery(),
                    "답변은: " + promptComponent.getAnswer()));
        }

        return new GeneratedDialogues(0, generatedDialogues);
    }
}
