package com.nexterview.server.service;

import static java.util.stream.Collectors.toMap;

import com.nexterview.server.domain.Dialogue;
import com.nexterview.server.domain.Interview;
import com.nexterview.server.domain.InterviewType;
import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptAnswer;
import com.nexterview.server.domain.PromptQuery;
import com.nexterview.server.domain.User;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.InterviewRepository;
import com.nexterview.server.repository.PromptQueryRepository;
import com.nexterview.server.repository.PromptRepository;
import com.nexterview.server.service.dto.request.DialogueRequest;
import com.nexterview.server.service.dto.request.GuestInterviewRequest;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import com.nexterview.server.service.dto.request.UserInterviewRequest;
import com.nexterview.server.service.dto.response.InterviewDto;
import com.nexterview.server.service.dto.response.InterviewTypeDto;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewService {

    private final AuthenticatedUserContext authenticatedUserContext;
    private final PromptRepository promptRepository;
    private final PromptQueryRepository promptQueryRepository;
    private final InterviewRepository interviewRepository;

    @Transactional
    public InterviewDto saveUserInterview(UserInterviewRequest request) {
        User user = authenticatedUserContext.getUser();
        Interview interview = Interview.createUserInterview(request.title(), user);
        return saveInterview(interview, request.promptId(), request.promptAnswers(), request.dialogues());
    }

    @Transactional
    public InterviewDto saveGuestInterview(GuestInterviewRequest request) {
        Interview interview = Interview.createGuestInterview(request.title(), request.guestPassword());
        return saveInterview(interview, request.promptId(), request.promptAnswers(), request.dialogues());
    }

    private InterviewDto saveInterview(
            Interview interview, Long promptId, List<PromptAnswerRequest> promptAnswers, List<DialogueRequest> dialogues
    ) {
        createPromptAnswers(interview, promptId, promptAnswers);
        createDialogues(interview, dialogues);
        interviewRepository.save(interview);

        return InterviewDto.of(interview);
    }

    private void createPromptAnswers(
            Interview interview, Long promptId, List<PromptAnswerRequest> requests
    ) {
        Prompt prompt = findPromptById(promptId);
        List<PromptQuery> queries = promptQueryRepository.findAllByPrompt(prompt);

        Map<Long, String> answers = requests.stream()
                .collect(toMap(PromptAnswerRequest::promptQueryId, PromptAnswerRequest::answer));
        for (PromptQuery query : queries) {
            String answer = answers.getOrDefault(query.getId(), "");
            new PromptAnswer(answer, query, interview);
        }
    }

    private void createDialogues(Interview interview, List<DialogueRequest> requests) {
        requests.forEach(dialogue -> new Dialogue(dialogue.question(), dialogue.answer(), interview));
    }

    private Prompt findPromptById(Long promptId) {
        return promptRepository.findById(promptId)
                .orElseThrow(() -> new NexterviewException(NexterviewErrorCode.PROMPT_NOT_FOUND, promptId));
    }

    public InterviewTypeDto getInterviewType(Long interviewTypeId) {
        Interview interview = findInterviewEntityById(interviewTypeId);
        InterviewType interviewType = interview.getInterviewType();

        return InterviewTypeDto.of(interviewType);
    }

    public InterviewDto findById(Long interviewId) {
        Interview interview = findInterviewEntityById(interviewId);
        return InterviewDto.of(interview);
    }

    private Interview findInterviewEntityById(Long interviewId) {
        return interviewRepository.findById(interviewId)
                .orElseThrow(() -> new NexterviewException(NexterviewErrorCode.INTERVIEW_NOT_FOUND, interviewId));
    }
}
