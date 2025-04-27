package com.nexterview.server.util;

import com.nexterview.server.domain.Interview;
import com.nexterview.server.domain.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InterviewFixture {

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
