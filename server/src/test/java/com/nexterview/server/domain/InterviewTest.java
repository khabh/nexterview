package com.nexterview.server.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.util.UserFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InterviewTest {

    @Nested
    class UserInterview {

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", " "})
        void 제목이_비어있으면_예외를_던진다(String title) {
            assertThatThrownBy(() -> Interview.createUserInterview(title, new User()))
                    .isInstanceOf(NexterviewException.class)
                    .hasMessageContaining("인터뷰 제목이 유효하지 않습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {"1", "1234567890123456"})
        void 제목_길이가_유효하지_않으면_예외를_던진다(String title) {
            assertThatThrownBy(() -> Interview.createUserInterview(title, new User()))
                    .isInstanceOf(NexterviewException.class)
                    .hasMessageContaining("인터뷰 제목이 유효하지 않습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Valid Title"})
        void 유효한_제목과_User를_넣으면_User_인터뷰가_정상적으로_생성된다(String title) {
            User user = new User();
            Interview interview = Interview.createUserInterview(title, user);
            assertThat(interview.getTitle()).isEqualTo(title);
            assertThat(interview.getUser()).isEqualTo(user);
        }

        @Test
        void User가_없으면_예외를_던진다() {
            assertThatThrownBy(() -> Interview.createUserInterview("title", null))
                    .isInstanceOf(NexterviewException.class);
        }

        @Test
        void 인터뷰_타입을_올바르게_반환한다() {
            Interview interview = Interview.createUserInterview("Valid Title", new User());

            InterviewType result = interview.getInterviewType();

            assertThat(result).isEqualTo(InterviewType.USER);
        }

        @Test
        void 유효하지_않은_사용자_에게_인터뷰_소유권을_검증하면_예외를_던진다() {
            User validUser = UserFixture.createUser("aaaa@gmail.com", "aaaa");
            User invalidUser = UserFixture.createUser("bbbb@gmail.com", "bbbb");
            Interview interview = Interview.createUserInterview("Valid Title", validUser);

            assertThatThrownBy(() -> interview.validateOwner(invalidUser))
                    .isInstanceOf(NexterviewException.class)
                    .hasMessageContaining(NexterviewErrorCode.INVALID_INTERVIEW_ACCESS.getMessage());
        }

        @Test
        void 유효한_사용자_에게_인터뷰_소유권을_검증하면_정상적으로_통과한다() {
            User validUser = UserFixture.createUser("aaaa@gmail.com", "aaaa");
            Interview interview = Interview.createUserInterview("Valid Title", validUser);

            interview.validateOwner(validUser);
        }
    }

    @Nested
    class GuestInterview {

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", " "})
        void 제목이_비어있으면_예외를_던진다(String title) {
            assertThatThrownBy(() -> Interview.createGuestInterview(title, "1234"))
                    .isInstanceOf(NexterviewException.class)
                    .hasMessageContaining("인터뷰 제목이 유효하지 않습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {"1", "1234567890123456"})
        void 제목_길이가_유효하지_않으면_예외를_던진다(String title) {
            assertThatThrownBy(() -> Interview.createGuestInterview(title, "1234"))
                    .isInstanceOf(NexterviewException.class)
                    .hasMessageContaining("인터뷰 제목이 유효하지 않습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Valid Title"})
        void 유효한_제목과_Guest_비밀번호를_넣으면_Guest_인터뷰가_정상적으로_생성된다(String title) {
            String guestPassword = "1234";
            Interview interview = Interview.createGuestInterview(title, guestPassword);
            assertThat(interview.getTitle()).isEqualTo(title);
            assertThat(interview.getGuestPassword()).isEqualTo(guestPassword);
        }

        @ParameterizedTest
        @ValueSource(strings = {"123", "123456", "abcd", "12ab"})
        void guestPassword가_유효하지_않으면_예외를_던진다(String guestPassword) {
            assertThatThrownBy(() -> Interview.createGuestInterview("Valid Title", guestPassword))
                    .isInstanceOf(NexterviewException.class)
                    .hasMessageContaining("게스트용 인터뷰 비밀번호가 유효하지 않습니다:");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Valid Title"})
        void guestPassword가_없으면_예외를_던진다(String title) {
            assertThatThrownBy(() -> Interview.createGuestInterview(title, null))
                    .isInstanceOf(NexterviewException.class)
                    .hasMessageContaining("게스트용 인터뷰 비밀번호가 유효하지 않습니다:");
        }

        @Test
        void 인터뷰_타입을_올바르게_반환한다() {
            Interview interview = Interview.createGuestInterview("Valid Title", "1234");

            InterviewType result = interview.getInterviewType();

            assertThat(result).isEqualTo(InterviewType.GUEST);
        }

        @Test
        void 잘못된_게스트_비밀번호_로_인터뷰_비밀번호를_검증하면_예외를_던진다() {
            String validGuestPassword = "1234";
            String invalidPassword = "wrongPassword";
            Interview interview = Interview.createGuestInterview("Valid Title", validGuestPassword);

            assertThatThrownBy(() -> interview.validatePassword(invalidPassword))
                    .isInstanceOf(NexterviewException.class)
                    .hasMessageContaining(NexterviewErrorCode.INTERVIEW_GUEST_PASSWORD_MISMATCH.getMessage());
        }

        @Test
        void 올바른_게스트_비밀번호_로_인터뷰_비밀번호를_검증하면_정상적으로_통과한다() {
            String validGuestPassword = "1234";
            Interview interview = Interview.createGuestInterview("Valid Title", validGuestPassword);

            interview.validatePassword(validGuestPassword);
        }
    }
}

