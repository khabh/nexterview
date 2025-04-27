package com.nexterview.server.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.util.InterviewFixture;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PromptAnswerTest {

    @ParameterizedTest
    @ValueSource(strings = {"1",
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901"})
    void 답변의_길이가_유효하지_않은_경우_예외를_던진다(String answer) {
        Prompt prompt = new Prompt("주제", "지시문입니다.");
        PromptQuery query = new PromptQuery("쿼리입니다.", prompt);
        Interview interview = InterviewFixture.createUserInterview();

        assertThatThrownBy(() -> new PromptAnswer(answer, query, interview))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("프롬프트 답변이 유효하지 않습니다");
    }
}
