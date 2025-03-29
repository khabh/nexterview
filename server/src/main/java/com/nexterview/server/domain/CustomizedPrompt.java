package com.nexterview.server.domain;

import static java.util.stream.Collectors.toMap;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import com.nexterview.server.service.dto.response.PromptDto;
import com.nexterview.server.service.dto.response.PromptQueryDto;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomizedPrompt {

    private static final String COMPONENT_DELIM = ", ";
    private static final String COMPONENT_FORMAT = "[%s: %s]";

    private final String instruction;
    private final List<PromptComponent> promptComponents;

    public CustomizedPrompt(String instruction, List<PromptComponent> promptComponents) {
        if (promptComponents == null || promptComponents.isEmpty()) {
            throw new NexterviewException(NexterviewErrorCode.CUSTOMIZED_PROMPT_INVALID);
        }
        this.instruction = instruction;
        this.promptComponents = promptComponents;
    }

    public static CustomizedPrompt of(PromptDto promptDto, List<PromptAnswerRequest> promptAnswers) {
        String instruction = promptDto.instruction();
        Map<Long, String> promptQueries = promptDto.queries().stream()
                .collect(toMap(PromptQueryDto::id, PromptQueryDto::query));
        List<PromptComponent> promptComponents = createPromptComponents(promptAnswers, promptQueries);

        return new CustomizedPrompt(instruction, promptComponents);
    }

    private static List<PromptComponent> createPromptComponents(
            List<PromptAnswerRequest> promptAnswers, Map<Long, String> promptQueries
    ) {
        return promptAnswers.stream()
                .map(promptAnswer -> createPromptComponent(promptAnswer, promptQueries))
                .flatMap(Optional::stream)
                .toList();
    }

    private static Optional<PromptComponent> createPromptComponent(
            PromptAnswerRequest promptAnswer, Map<Long, String> promptQueries
    ) {
        if (!promptQueries.containsKey(promptAnswer.promptQueryId())) {
            return Optional.empty();
        }
        String promptQuery = promptQueries.get(promptAnswer.promptQueryId());
        PromptComponent promptComponent = new PromptComponent(promptQuery, promptAnswer.answer());

        return Optional.of(promptComponent);
    }

    public String getRawPrompt() {
        String formattedComponents = promptComponents.stream()
                .map(component -> String.format(COMPONENT_FORMAT, component.getQuestion(), component.getAnswer()))
                .collect(Collectors.joining(COMPONENT_DELIM));

        return instruction + formattedComponents;
    }
}
