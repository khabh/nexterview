package com.nexterview.server.domain;

import com.nexterview.server.domain.validator.StringInvalidException;
import com.nexterview.server.domain.validator.StringValidator;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
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
public class PromptAnswer {

    private static final StringValidator ANSWER_VALIDATOR = StringValidator.emptyWithRange(2, 100);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String answer;

    @ManyToOne
    @JoinColumn(nullable = false)
    private PromptQuery query;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Interview interview;

    public PromptAnswer(String answer, PromptQuery query, Interview interview) {
        this(null, answer, query, interview);
    }

    public PromptAnswer(Long id, String answer, PromptQuery query, Interview interview) {
        validateAnswer(answer);
        validateQuery(query);
        validateInterview(interview);
        this.id = id;
        this.answer = answer;
        this.query = query;
        this.interview = interview;

        interview.addPromptAnswer(this);
    }

    private void validateAnswer(String answer) {
        try {
            ANSWER_VALIDATOR.validate(answer);
        } catch (StringInvalidException exception) {
            throw new NexterviewException(NexterviewErrorCode.PROMPT_ANSWER_INVALID, exception.getMessage());
        }
    }

    private void validateQuery(PromptQuery query) {
        if (query == null) {
            throw new NexterviewException(NexterviewErrorCode.PROMPT_QUERY_NULL);
        }
    }

    private void validateInterview(Interview interview) {
        if (interview == null) {
            throw new NexterviewException(NexterviewErrorCode.INTERVIEW_NULL);
        }
    }
}
