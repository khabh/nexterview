package com.nexterview.server.domain;

import com.nexterview.server.domain.validator.StringInvalidException;
import com.nexterview.server.domain.validator.StringValidator;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Interview {

    private static final StringValidator TITLE_VALIDATOR = StringValidator.notEmptyWithRange(2, 15);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PromptAnswer> promptAnswers = new ArrayList<>();

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dialogue> dialogues = new ArrayList<>();

    public Interview(String title) {
        this(null, title);
    }

    public Interview(Long id, String title) {
        validateTitle(title);
        this.id = id;
        this.title = title;
    }

    private void validateTitle(String title) {
        try {
            TITLE_VALIDATOR.validate(title);
        } catch (StringInvalidException exception) {
            throw new NexterviewException(NexterviewErrorCode.INTERVIEW_TITLE_INVALID, exception.getMessage());
        }
    }

    public void addPromptAnswer(PromptAnswer promptAnswer) {
        promptAnswers.add(promptAnswer);
    }

    public void addDialogue(Dialogue dialogue) {
        dialogues.add(dialogue);
    }
}
