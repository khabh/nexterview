package com.nexterview.server.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.exception.NexterviewException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InterviewTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", " "})
    void 제목이_비어있으면_예외를_던진다(String title) {
        assertThatThrownBy(() -> new Interview(title))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("인터뷰 제목이 유효하지 않습니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "1234567890123456"})
    void 제목_길이가_유효하지_않으면_예외를_던진다(String title) {
        assertThatThrownBy(() -> new Interview(title))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("인터뷰 제목이 유효하지 않습니다");
    }
}
