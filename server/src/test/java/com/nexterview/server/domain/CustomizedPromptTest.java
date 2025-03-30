package com.nexterview.server.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CustomizedPromptTest {

    @Test
    void 올바르게_인스턴스를_생성한다() {
        Prompt prompt = new Prompt("Topic", "Instruction");
        List<PromptQuery> promptQueries = List.of(new PromptQuery(1L, "Query1", prompt),
                new PromptQuery(2L, "Query2", prompt));
        Map<Long, String> promptAnswers = Map.of(1L, "Answer1", 2L, "Answer2");

        CustomizedPrompt customizedPrompt = CustomizedPrompt.of(prompt, promptQueries, promptAnswers);

        assertThat(customizedPrompt.getInstruction()).isEqualTo(prompt.getInstruction());
        assertThat(customizedPrompt.getPromptComponents())
                .containsExactlyInAnyOrder(
                        new PromptComponent("Query1", "Answer1"),
                        new PromptComponent("Query2", "Answer2")
                );
    }

    @Test
    void 프롬프트_텍스트를_생성한다() {
        Prompt prompt = new Prompt("Topic", "This is an instruction. ");
        List<PromptQuery> promptQueries = List.of(new PromptQuery(1L, "What is your name?", prompt),
                new PromptQuery(2L, "Where do you live?", prompt));
        Map<Long, String> promptAnswers = Map.of(1L, "Juha", 2L, "Korea");
        CustomizedPrompt customizedPrompt = CustomizedPrompt.of(prompt, promptQueries, promptAnswers);

        String rawPrompt = customizedPrompt.getRawPrompt();

        assertThat(rawPrompt).isEqualTo(
                "This is an instruction. [What is your name?: Juha], [Where do you live?: Korea]");
    }

    @Test
    void 컴포넌트가_비어있으면_예외를_던진다() {
        Prompt prompt = new Prompt("Topic", "This is an instruction. ");
        List<PromptQuery> promptQueries = List.of(new PromptQuery(1L, "What is your name?", prompt));
        Map<Long, String> promptAnswers = Map.of();

        assertThatThrownBy(() -> CustomizedPrompt.of(prompt, promptQueries, promptAnswers))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.PROMPT_ANSWER_REQUIRED.getMessage());
    }
}
