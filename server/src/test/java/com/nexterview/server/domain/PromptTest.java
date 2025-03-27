package com.nexterview.server.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.exception.NexterviewException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PromptTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", " "})
    void 주제가_비어있으면_예외를_던진다(String topic) {
        String instruction = "지시문입니다.";

        assertThatThrownBy(() -> new Prompt(topic, instruction))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("프롬프트 주제가 유효하지 않습니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "1234567890123456"})
    void 주제의_길이가_유효하지_않은_경우_예외를_던진다(String topic) {
        String instruction = "지시문입니다.";

        assertThatThrownBy(() -> new Prompt(topic, instruction))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("프롬프트 주제가 유효하지 않습니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", " "})
    void 지시문이_비어있으면_예외를_던진다(String instruction) {
        String topic = "주제";

        assertThatThrownBy(() -> new Prompt(topic, instruction))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("프롬프트 지시문이 유효하지 않습니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "12345678901234567890123456789012345678901"})
    void 지시문의_길이가_유효하지_않은_경우_예외를_던진다(String instruction) {
        String topic = "주제";

        assertThatThrownBy(() -> new Prompt(topic, instruction))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("프롬프트 지시문이 유효하지 않습니다");
    }
}
