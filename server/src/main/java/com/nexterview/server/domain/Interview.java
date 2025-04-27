package com.nexterview.server.domain;

import com.nexterview.server.domain.validator.StringInvalidException;
import com.nexterview.server.domain.validator.StringValidator;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Interview {

    private static final StringValidator TITLE_VALIDATOR = StringValidator.notEmptyWithRange(2, 15);
    private static final StringValidator GUEST_PASSWORD_VALIDATOR = StringValidator.patternWithRange(4, 4,
            "^[0-9]{4}$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String guestPassword;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PromptAnswer> promptAnswers = new ArrayList<>();

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dialogue> dialogues = new ArrayList<>();

    public Interview(String title) {
        validateTitle(title);
        this.title = title;
    }

    private Interview(Long id, String title, User user, String guestPassword) {
        validateInterview(title, user, guestPassword);
        this.id = id;
        this.title = title;
        this.user = user;
        this.guestPassword = guestPassword;
    }

    public static Interview createUserInterview(String title, User user) {
        return new Interview(null, title, user, null);
    }

    public static Interview createGuestInterview(String title, String guestPassword) {
        return new Interview(null, title, null, guestPassword);
    }

    private void validateInterview(String title, User user, String guestPassword) {
        validateTitle(title);
        validateUserAndGuestPassword(user, guestPassword);
        if (user == null) {
            validateGuestPassword(guestPassword);
        }
    }

    private void validateTitle(String title) {
        try {
            TITLE_VALIDATOR.validate(title);
        } catch (StringInvalidException exception) {
            throw new NexterviewException(NexterviewErrorCode.INTERVIEW_TITLE_INVALID, exception.getMessage());
        }
    }

    private void validateUserAndGuestPassword(User user, String guestPassword) {
        if (user != null && guestPassword != null) {
            throw new NexterviewException(NexterviewErrorCode.USER_AND_GUEST_PASSWORD_CONFLICT);
        }
    }

    private void validateGuestPassword(String guestPassword) {
        try {
            GUEST_PASSWORD_VALIDATOR.validate(guestPassword);
        } catch (StringInvalidException exception) {
            throw new NexterviewException(NexterviewErrorCode.INTERVIEW_GUEST_PASSWORD_INVALID, exception.getMessage());
        }
    }

    public void addPromptAnswer(PromptAnswer promptAnswer) {
        promptAnswers.add(promptAnswer);
    }

    public void addDialogue(Dialogue dialogue) {
        dialogues.add(dialogue);
    }
}
