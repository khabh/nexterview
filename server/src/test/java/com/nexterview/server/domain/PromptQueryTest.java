package com.nexterview.server.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.exception.NexterviewException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PromptQueryTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", " "})
    void 질문이_비어있으면_예외를_던진다(String query) {
        Prompt prompt = new Prompt("주제", "지시문입니다.");

        assertThatThrownBy(() -> new PromptQuery(query, prompt))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("프롬프트 질문이 유효하지 않습니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "123456789012345678901"})
    void 질문의_길이가_유효하지_않은_경우_예외를_던진다(String query) {
        Prompt prompt = new Prompt("주제", "지시문입니다.");

        assertThatThrownBy(() -> new PromptQuery(query, prompt))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("프롬프트 질문이 유효하지 않습니다");
    }
}
