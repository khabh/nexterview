package com.nexterview.server.service;

import static java.util.stream.Collectors.toMap;

import com.nexterview.server.domain.CustomizedPrompt;
import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptQuery;
import com.nexterview.server.domain.TokenQuota;
import com.nexterview.server.domain.User;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.PromptQueryRepository;
import com.nexterview.server.repository.PromptRepository;
import com.nexterview.server.repository.TokenQuotaRepository;
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
    private final AuthenticatedUserContext authenticatedUserContext;
    private final PromptAccessLimiter promptAccessLimiter;
    private final PromptRepository promptRepository;
    private final PromptQueryRepository promptQueryRepository;
    private final TokenQuotaRepository tokenQuotaRepository;

    public List<GeneratedDialogueDto> generateDialoguesForGuest(GenerateDialoguesRequest request, String clientIp) {
        promptAccessLimiter.checkAccessOrThrow(clientIp);
        try {
            GeneratedDialogues dialogues = generateDialogues(request);
            promptAccessLimiter.commitAccess(clientIp);

            return dialogues.dialogues();
        } catch (NexterviewException exception) {
            promptAccessLimiter.rollbackAccess(clientIp);

            throw exception;
        }
    }

    @Transactional
    public List<GeneratedDialogueDto> generateDialoguesForUser(GenerateDialoguesRequest request) {
        TokenQuota tokenQuota = getOrCreateUserTokenQuota();
        tokenQuota.validateQuotaAvailable();
        GeneratedDialogues generatedDialogues = generateDialogues(request);
        int tokenAmount = generatedDialogues.totalTokens();
        tokenQuota.useQuota(tokenAmount);

        return generatedDialogues.dialogues();
    }

    private GeneratedDialogues generateDialogues(GenerateDialoguesRequest request) {
        Prompt prompt = findById(request.promptId());
        List<PromptQuery> promptQueries = promptQueryRepository.findAllByPrompt(prompt);
        Map<Long, String> promptAnswers = request.promptAnswers().stream()
                .collect(toMap(PromptAnswerRequest::promptQueryId, PromptAnswerRequest::answer));
        CustomizedPrompt customizedPrompt = CustomizedPrompt.of(prompt, promptQueries, promptAnswers);

        return dialogueGenerator.generate(customizedPrompt);
    }

    private TokenQuota getOrCreateUserTokenQuota() {
        User user = authenticatedUserContext.getUser();

        return tokenQuotaRepository.findByUser(user)
                .orElseGet(() -> createTokenQuota(user));
    }

    private TokenQuota createTokenQuota(User user) {
        TokenQuota tokenQuota = TokenQuota.createMaxQuota(user);

        return tokenQuotaRepository.save(tokenQuota);
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
