package com.nexterview.server.exception;

import lombok.Getter;

@Getter
public enum NexterviewErrorCode {

    // Domain
    DIALOGUE_QUESTION_INVALID("인터뷰 문답의 질문이 유효하지 않습니다: %s"),
    DIALOGUE_ANSWER_INVALID("인터뷰 문답의 답변이 유효하지 않습니다: %s"),
    PROMPT_TOPIC_INVALID("프롬프트 주제가 유효하지 않습니다: %s"),
    PROMPT_INSTRUCTION_INVALID("프롬프트 지시문이 유효하지 않습니다: %s"),
    PROMPT_QUERY_INVALID("프롬프트 질문이 유효하지 않습니다: %s"),
    PROMPT_QUERY_NULL("프롬프트 질문은 비어 있을 수 없습니다."),
    PROMPT_ANSWER_INVALID("프롬프트 답변이 유효하지 않습니다: %s"),
    PROMPT_NULL("프롬프트는 비어 있을 수 없습니다."),
    INTERVIEW_NULL("인터뷰는 비어 있을 수 없습니다."),
    INTERVIEW_TITLE_INVALID("인터뷰 제목이 유효하지 않습니다: %s"),
    PROMPT_ANSWER_REQUIRED("프롬프트 질문에 대한 답변이 하나 이상 필요합니다."),

    // Service
    PROMPT_NOT_FOUND("ID가 %d인 프롬프트를 찾을 수 없습니다."),
    INTERVIEW_NOT_FOUND("ID가 %d인 인터뷰를 찾을 수 없습니다."),
    CHAT_API_UNAVAILABLE("AI 응답을 받을 수 없습니다."),

    // DTO
    ARGUMENT_INVALID("요청 형식이 올바르지 않습니다: "),
    ;

    private final String message;

    NexterviewErrorCode(String message) {
        this.message = message;
    }
}
