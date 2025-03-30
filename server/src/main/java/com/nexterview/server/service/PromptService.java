package com.nexterview.server.service;

import static java.util.stream.Collectors.toMap;

import com.nexterview.server.domain.CustomizedPrompt;
import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptQuery;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.PromptQueryRepository;
import com.nexterview.server.repository.PromptRepository;
import com.nexterview.server.service.dto.request.GenerateDialoguesRequest;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import com.nexterview.server.service.dto.response.GeneratedDialogueDto;
import com.nexterview.server.service.dto.response.PromptDto;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PromptService {

    private final DialogueGenerator dialogueGenerator;
    private final PromptRepository promptRepository;
    private final PromptQueryRepository promptQueryRepository;

    public List<GeneratedDialogueDto> generateDialogues(GenerateDialoguesRequest request) {
        Prompt prompt = findById(request.promptId());
        List<PromptQuery> promptQueries = promptQueryRepository.findAllByPrompt(prompt);
        Map<Long, String> promptAnswers = request.promptAnswers().stream()
                .collect(toMap(PromptAnswerRequest::promptQueryId, PromptAnswerRequest::answer));
        CustomizedPrompt customizedPrompt = CustomizedPrompt.of(prompt, promptQueries, promptAnswers);

        return dialogueGenerator.generate(customizedPrompt);
    }

    private Prompt findById(Long id) {
        return promptRepository.findById(id)
                .orElseThrow(() -> new NexterviewException(NexterviewErrorCode.PROMPT_NOT_FOUND, id));
    }

    public List<PromptDto> findAll() {
        return promptRepository.findAll()
                .stream()
                .map(this::promptToDto)
                .toList();
    }

    private PromptDto promptToDto(Prompt prompt) {
        List<PromptQuery> promptQueries = promptQueryRepository.findAllByPrompt(prompt);

        return PromptDto.of(prompt, promptQueries);
    }
}
