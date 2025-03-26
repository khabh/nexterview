package com.nexterview.server.domain;

import com.nexterview.server.domain.validator.StringInvalidException;
import com.nexterview.server.domain.validator.StringValidator;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PromptQuery {

    private static final StringValidator QUESTION_VALIDATOR = StringValidator.notEmptyWithRange(2, 20);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String query;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Prompt prompt;

    public PromptQuery(String query, Prompt prompt) {
        this(null, query, prompt);
    }

    public PromptQuery(Long id, String query, Prompt prompt) {
        validateQuery(query);
        validatePrompt(prompt);
        this.id = id;
        this.query = query;
        this.prompt = prompt;
    }

    private void validatePrompt(Prompt prompt) {
        if (prompt == null) {
            throw new NexterviewException(NexterviewErrorCode.PROMPT_NULL);
        }
    }

    private void validateQuery(String query) {
        try {
            QUESTION_VALIDATOR.validate(query);
        } catch (StringInvalidException exception) {
            throw new NexterviewException(NexterviewErrorCode.PROMPT_QUERY_INVALID, exception.getMessage());
        }
    }
}
