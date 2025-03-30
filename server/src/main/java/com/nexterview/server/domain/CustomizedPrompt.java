package com.nexterview.server.domain;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class CustomizedPrompt {

    private static final String COMPONENT_DELIM = ", ";
    private static final String COMPONENT_FORMAT = "[%s: %s]";

    private final String instruction;
    private final List<PromptComponent> promptComponents;

    public CustomizedPrompt(String instruction, List<PromptComponent> promptComponents) {
        if (promptComponents == null || promptComponents.isEmpty()) {
            throw new NexterviewException(NexterviewErrorCode.PROMPT_ANSWER_REQUIRED);
        }
        this.instruction = instruction;
        this.promptComponents = promptComponents;
    }

    public static CustomizedPrompt of(Prompt prompt, List<PromptQuery> promptQueries, Map<Long, String> promptAnswers) {
        String instruction = prompt.getInstruction();
        List<PromptComponent> promptComponents = createPromptComponents(promptQueries, promptAnswers);

        return new CustomizedPrompt(instruction, promptComponents);
    }

    private static List<PromptComponent> createPromptComponents(
            List<PromptQuery> promptQueries, Map<Long, String> promptAnswers
    ) {
        return promptQueries.stream()
                .filter(promptQuery -> promptAnswers.containsKey(promptQuery.getId()))
                .map(promptQuery -> new PromptComponent(promptQuery.getQuery(), promptAnswers.get(promptQuery.getId())))
                .toList();
    }

    public String getRawPrompt() {
        String formattedComponents = promptComponents.stream()
                .map(component -> String.format(COMPONENT_FORMAT, component.getQuery(), component.getAnswer()))
                .collect(Collectors.joining(COMPONENT_DELIM));

        return instruction + formattedComponents;
    }
}
