package com.nexterview.server.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.exception.NexterviewException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DialogueTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", " "})
    void 질문이_비어있으면_예외를_던진다(String question) {
        String answer = "질문 대답";
        Interview interview = new Interview("제목");

        assertThatThrownBy(() -> new Dialogue(question, answer, interview))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("인터뷰 문답의 질문이 유효하지 않습니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "123456789012345678901234567890123456789012345678901"})
    void 질문의_길이가_유효하지_않은_경우_예외를_던진다(String question) {
        String answer = "질문 대답";
        Interview interview = new Interview("제목");

        assertThatThrownBy(() -> new Dialogue(question, answer, interview))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("인터뷰 문답의 질문이 유효하지 않습니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", " "})
    void 답변이_비어_있어도_예외가_발생하지_않는다(String answer) {
        String question = "인터뷰 질문";
        Interview interview = new Interview("제목");

        assertThatCode(() -> new Dialogue(question, answer, interview))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901"})
    void 답변의_길이가_유효하지_않은_경우_예외를_던진다(String answer) {
        String question = "인터뷰 질문";
        Interview interview = new Interview("제목");

        assertThatThrownBy(() -> new Dialogue(question, answer, interview))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("인터뷰 문답의 답변이 유효하지 않습니다");
    }
}
