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
import com.nexterview.server.service.dto.request.GuestInterviewDeleteRequest;
import com.nexterview.server.service.dto.request.GuestInterviewRequest;
import com.nexterview.server.service.dto.request.GuestInterviewUpdateRequest;
import com.nexterview.server.service.dto.request.InterviewPasswordRequest;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import com.nexterview.server.service.dto.request.UserInterviewRequest;
import com.nexterview.server.service.dto.request.UserInterviewUpdateRequest;
import com.nexterview.server.service.dto.response.InterviewDto;
import com.nexterview.server.service.dto.response.InterviewPreviewDto;
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

    @Transactional
    public InterviewDto updateUserInterview(UserInterviewUpdateRequest request) {
        Interview interview = findInterviewWithCurrentUser(request.interviewId());
        interview.setTitle(request.title());
        updateInterviewPromptAnswers(interview, request.promptId(), request.promptAnswers());
        updateInterviewDialogues(interview, request.dialogues());
        interviewRepository.save(interview);

        return InterviewDto.of(interview);
    }

    @Transactional
    public InterviewDto updateGuestInterview(GuestInterviewUpdateRequest request) {
        Interview interview = findInterviewWithPassword(request.interviewId(), request.guestPassword());
        interview.setTitle(request.title());
        updateInterviewPromptAnswers(interview, request.promptId(), request.promptAnswers());
        updateInterviewDialogues(interview, request.dialogues());
        interviewRepository.save(interview);

        return InterviewDto.of(interview);
    }

    private void updateInterviewPromptAnswers(
            Interview interview, Long promptId, List<PromptAnswerRequest> promptAnswers
    ) {
        interview.clearPromptAnswers();
        createPromptAnswers(interview, promptId, promptAnswers);
    }

    private void updateInterviewDialogues(Interview interview, List<DialogueRequest> dialogues) {
        interview.clearDialogues();
        createDialogues(interview, dialogues);
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

    public InterviewDto findUserInterview(Long interviewId) {
        Interview interview = findInterviewWithCurrentUser(interviewId);

        return InterviewDto.of(interview);
    }

    public List<InterviewPreviewDto> findUserInterviews() {
        User user = authenticatedUserContext.getUser();

        return interviewRepository.findAllByUser(user)
                .stream()
                .map(InterviewPreviewDto::of)
                .toList();
    }

    public InterviewDto findGuestInterview(Long interviewId, InterviewPasswordRequest request) {
        Interview interview = findInterviewWithPassword(interviewId, request.password());
        return InterviewDto.of(interview);
    }

    @Transactional
    public void deleteUserInterview(Long interviewId) {
        Interview interview = findInterviewWithCurrentUser(interviewId);
        interviewRepository.delete(interview);
    }

    @Transactional
    public void deleteGuestInterview(GuestInterviewDeleteRequest request) {
        Interview interview = findInterviewWithPassword(request.interviewId(), request.password());
        interviewRepository.delete(interview);
    }

    private Interview findInterviewWithCurrentUser(Long interviewId) {
        User user = authenticatedUserContext.getUser();
        Interview interview = findInterview(interviewId, InterviewType.USER);
        interview.validateOwner(user);

        return interview;
    }

    private Interview findInterviewWithPassword(Long interviewId, String password) {
        Interview interview = findInterview(interviewId, InterviewType.GUEST);
        interview.validatePassword(password);

        return interview;
    }

    private Interview findInterview(Long interviewId, InterviewType interviewType) {
        Interview interview = findInterviewEntityById(interviewId);
        if (interview.getInterviewType() != interviewType) {
            throw new NexterviewException(NexterviewErrorCode.INVALID_INTERVIEW_TYPE);
        }

        return interview;
    }

    private Interview findInterviewEntityById(Long interviewId) {
        return interviewRepository.findById(interviewId)
                .orElseThrow(() -> new NexterviewException(NexterviewErrorCode.INTERVIEW_NOT_FOUND, interviewId));
    }
}
