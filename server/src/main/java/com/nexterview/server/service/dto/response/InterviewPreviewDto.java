package com.nexterview.server.service.dto.response;

import com.nexterview.server.domain.Dialogue;
import com.nexterview.server.domain.Interview;
import java.util.List;

public record InterviewPreviewDto(
        Long id,
        String title,
        List<String> questions
) {

    public static InterviewPreviewDto of(Interview interview) {
        List<String> questions = interview.getDialogues().stream()
                .map(Dialogue::getQuestion)
                .toList();

        return new InterviewPreviewDto(interview.getId(), interview.getTitle(), questions);
    }
}
