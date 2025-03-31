package com.nexterview.server.service;

import static java.util.stream.Collectors.toMap;

import com.nexterview.server.domain.Dialogue;
import com.nexterview.server.domain.Interview;
import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptAnswer;
import com.nexterview.server.domain.PromptQuery;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.InterviewRepository;
import com.nexterview.server.repository.PromptQueryRepository;
import com.nexterview.server.repository.PromptRepository;
import com.nexterview.server.service.dto.request.DialogueRequest;
import com.nexterview.server.service.dto.request.InterviewRequest;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import com.nexterview.server.service.dto.response.InterviewDto;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewService {

    private final PromptRepository promptRepository;
    private final PromptQueryRepository promptQueryRepository;
    private final InterviewRepository interviewRepository;

    @Transactional
    public InterviewDto saveInterview(InterviewRequest request) {
        Interview interview = new Interview(request.title());

        Prompt prompt = findPromptById(request.promptId());
        List<PromptQuery> queries = promptQueryRepository.findAllByPrompt(prompt);
        List<PromptAnswer> answers = createPromptAnswers(interview, queries, request.promptAnswers());
        if (answers.isEmpty()) {
            throw new NexterviewException(NexterviewErrorCode.PROMPT_ANSWER_REQUIRED);
        }

        List<Dialogue> dialogues = createDialogues(interview, request.dialogues());

        interviewRepository.save(interview);

        return InterviewDto.of(interview, answers, dialogues);
    }


    private List<PromptAnswer> createPromptAnswers(
            Interview interview, List<PromptQuery> queries, List<PromptAnswerRequest> requests
    ) {
        Map<Long, PromptQuery> queriesById = queries.stream()
                .collect(toMap(PromptQuery::getId, Function.identity()));

        return requests.stream()
                .filter(answer -> queriesById.containsKey(answer.promptQueryId()))
                .map(answer -> new PromptAnswer(answer.answer(), queriesById.get(answer.promptQueryId()), interview))
                .toList();
    }

    private List<Dialogue> createDialogues(Interview interview, List<DialogueRequest> requests) {
        return requests.stream()
                .map(dialogue -> new Dialogue(dialogue.question(), dialogue.answer(), interview))
                .toList();
    }

    private Prompt findPromptById(Long promptId) {
        return promptRepository.findById(promptId)
                .orElseThrow(() -> new NexterviewException(NexterviewErrorCode.PROMPT_NOT_FOUND, promptId));
    }
}
