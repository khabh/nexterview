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
public class Dialogue {

    private static final StringValidator QUESTION_VALIDATOR = StringValidator.notEmptyWithRange(5, 50);
    private static final StringValidator ANSWER_VALIDATOR = StringValidator.emptyWithRange(5, 100);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Interview interview;

    public Dialogue(String question, String answer, Interview interview) {
        this(null, question, answer, interview);
    }

    public Dialogue(Long id, String question, String answer, Interview interview) {
        validateQuestion(question);
        validateAnswer(answer);
        validateInterview(interview);
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.interview = interview;

        interview.addDialogue(this);
    }

    private void validateQuestion(String question) {
        try {
            QUESTION_VALIDATOR.validate(question);
        } catch (StringInvalidException exception) {
            throw new NexterviewException(NexterviewErrorCode.DIALOGUE_QUESTION_INVALID, exception.getMessage());
        }
    }

    private void validateAnswer(String answer) {
        try {
            ANSWER_VALIDATOR.validate(answer);
        } catch (StringInvalidException exception) {
            throw new NexterviewException(NexterviewErrorCode.DIALOGUE_ANSWER_INVALID, exception.getMessage());
        }
    }

    private void validateInterview(Interview interview) {
        if (interview == null) {
            throw new NexterviewException(NexterviewErrorCode.INTERVIEW_NULL);
        }
    }
}
