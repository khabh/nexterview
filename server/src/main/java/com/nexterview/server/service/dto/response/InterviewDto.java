package com.nexterview.server.service.dto.response;

import com.nexterview.server.domain.Dialogue;
import com.nexterview.server.domain.Interview;
import com.nexterview.server.domain.PromptAnswer;
import java.util.List;

public record InterviewDto(
        Long id,
        String title,
        List<PromptAnswerDto> promptAnswers,
        List<DialogueDto> dialogues
) {

    public static InterviewDto of(Interview interview, List<PromptAnswer> promptAnswers, List<Dialogue> dialogues) {
        List<PromptAnswerDto> promptAnswerDtos = promptAnswers.stream()
                .map(PromptAnswerDto::of)
                .toList();
        List<DialogueDto> dialogueDtos = dialogues.stream()
                .map(DialogueDto::of)
                .toList();

        return new InterviewDto(interview.getId(), interview.getTitle(), promptAnswerDtos, dialogueDtos);
    }
}
