package com.nexterview.server.domain;

import com.nexterview.server.domain.validator.StringInvalidException;
import com.nexterview.server.domain.validator.StringValidator;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Prompt {

    private static final StringValidator TOPIC_VALIDATOR = StringValidator.notEmptyWithRange(2, 15);
    private static final StringValidator INSTRUCTION_VALIDATOR = StringValidator.notEmptyWithRange(5, 40);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String topic;

    @Column(nullable = false)
    private String instruction;

    public Prompt(String topic, String instruction) {
        this(null, topic, instruction);
    }

    public Prompt(Long id, String topic, String instruction) {
        validateTopic(topic);
        validateInstruction(instruction);
        this.id = id;
        this.topic = topic;
        this.instruction = instruction;
    }

    private void validateTopic(String topic) {
        try {
            TOPIC_VALIDATOR.validate(topic);
        } catch (StringInvalidException exception) {
            throw new NexterviewException(NexterviewErrorCode.PROMPT_TOPIC_INVALID, exception.getMessage());
        }
    }

    private void validateInstruction(String instruction) {
        try {
            INSTRUCTION_VALIDATOR.validate(instruction);
        } catch (StringInvalidException exception) {
            throw new NexterviewException(NexterviewErrorCode.PROMPT_INSTRUCTION_INVALID, exception.getMessage());
        }
    }
}
