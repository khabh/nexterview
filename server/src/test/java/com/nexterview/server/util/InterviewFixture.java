package com.nexterview.server.util;

import com.nexterview.server.domain.Interview;
import com.nexterview.server.domain.User;
import com.nexterview.server.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewFixture {

    private final InterviewRepository interviewRepository;
    private final UserFixture userFixture;

    public Interview getSavedUserInterview() {
        User user = userFixture.getSavedUser("abcd@gmail.com", "abcd", "potato!123");
        return getSavedUserInterview("제목", user);
    }

    public Interview getSavedUserInterview(String title, User user) {
        Interview interview = Interview.createUserInterview(title, user);
        return interviewRepository.save(interview);
    }

    public Interview getSavedGuestInterview() {
        return getSavedGuestInterview("제목", "1234");
    }

    public Interview getSavedGuestInterview(String title, String guestPassword) {
        Interview interview = Interview.createGuestInterview(title, guestPassword);
        return interviewRepository.save(interview);
    }

    public static Interview createUserInterview() {
        return createUserInterview("제목");
    }

    public static Interview createUserInterview(String title) {
        User user = User.of("abcd@gmail.com", "abcd", "potato!123", new BCryptPasswordEncoder());
        return Interview.createUserInterview(title, user);
    }

    public static Interview createGuestInterview() {
        return createGuestInterview("제목");
    }

    public static Interview createGuestInterview(String title) {
        return Interview.createGuestInterview(title, "1234");
    }
}
