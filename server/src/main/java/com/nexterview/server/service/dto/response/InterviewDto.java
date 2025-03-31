package com.nexterview.server.service.dto.response;

import com.nexterview.server.domain.Interview;
import java.util.List;

public record InterviewDto(
        Long id,
        String title,
        List<PromptAnswerDto> promptAnswers,
        List<DialogueDto> dialogues
) {

    public static InterviewDto of(Interview interview) {
        List<PromptAnswerDto> promptAnswerDtos = interview.getPromptAnswers().stream()
                .map(PromptAnswerDto::of)
                .toList();
        List<DialogueDto> dialogueDtos = interview.getDialogues().stream()
                .map(DialogueDto::of)
                .toList();

        return new InterviewDto(interview.getId(), interview.getTitle(), promptAnswerDtos, dialogueDtos);
    }
}
