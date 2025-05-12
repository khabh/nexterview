package com.nexterview.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.domain.Dialogue;
import com.nexterview.server.domain.Interview;
import com.nexterview.server.domain.InterviewType;
import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptAnswer;
import com.nexterview.server.domain.PromptQuery;
import com.nexterview.server.domain.User;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.DialogueRepository;
import com.nexterview.server.repository.InterviewRepository;
import com.nexterview.server.repository.PromptAnswerRepository;
import com.nexterview.server.repository.PromptQueryRepository;
import com.nexterview.server.repository.PromptRepository;
import com.nexterview.server.service.dto.request.DialogueRequest;
import com.nexterview.server.service.dto.request.GuestInterviewDeleteRequest;
import com.nexterview.server.service.dto.request.GuestInterviewRequest;
import com.nexterview.server.service.dto.request.GuestInterviewUpdateRequest;
import com.nexterview.server.service.dto.request.InterviewPasswordRequest;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import com.nexterview.server.service.dto.request.UserInterviewRequest;
import com.nexterview.server.service.dto.request.UserInterviewUpdateRequest;
import com.nexterview.server.service.dto.response.DialogueDto;
import com.nexterview.server.service.dto.response.InterviewDto;
import com.nexterview.server.service.dto.response.InterviewPreviewDto;
import com.nexterview.server.service.dto.response.InterviewTypeDto;
import com.nexterview.server.service.dto.response.PromptAnswerDto;
import com.nexterview.server.util.DatabaseCleaner;
import com.nexterview.server.util.InterviewFixture;
import com.nexterview.server.util.UserFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class InterviewServiceTest {

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private PromptRepository promptRepository;

    @Autowired
    private PromptQueryRepository promptQueryRepository;

    @Autowired
    private PromptAnswerRepository promptAnswerRepository;

    @Autowired
    private DialogueRepository dialogueRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private UserFixture userFixture;

    @Autowired
    private InterviewFixture interviewFixture;

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void 사용자_인터뷰를_저장한다() {
        User user = userFixture.getAuthenticatedUser("abcd@gmail.com", "potato", "potato123!");

        Prompt prompt = new Prompt("백엔드 면접", "백엔드 관련 질문을 생성해주세요.");
        promptRepository.save(prompt);

        PromptQuery query1 = new PromptQuery("가장 많이 사용한 언어는?", prompt);
        PromptQuery query2 = new PromptQuery("가장 자신 있는 기술은?", prompt);
        promptQueryRepository.saveAll(List.of(query1, query2));

        PromptAnswerRequest answer1 = new PromptAnswerRequest(query1.getId(), "자바입니다");
        PromptAnswerRequest answer2 = new PromptAnswerRequest(query2.getId(), "정권 찌르기");
        List<PromptAnswerRequest> answers = List.of(answer1, answer2);

        DialogueRequest dialogue1 = new DialogueRequest("Java의 장점은?", "객체지향적이고 플랫폼 독립적이다.");
        DialogueRequest dialogue2 = new DialogueRequest("Spring Boot의 핵심 기능은?", "핵심 기능");
        List<DialogueRequest> dialogues = List.of(dialogue1, dialogue2);

        UserInterviewRequest request = new UserInterviewRequest("백엔드 면접 인터뷰", prompt.getId(), answers, dialogues);

        InterviewDto result = interviewService.saveUserInterview(request);

        assertThat(result).isEqualTo(
                new InterviewDto(
                        1L,
                        "백엔드 면접 인터뷰",
                        List.of(
                                new PromptAnswerDto(1L, 1L, "가장 많이 사용한 언어는?", "자바입니다"),
                                new PromptAnswerDto(2L, 2L, "가장 자신 있는 기술은?", "정권 찌르기")),
                        List.of(
                                new DialogueDto(1L, "Java의 장점은?", "객체지향적이고 플랫폼 독립적이다."),
                                new DialogueDto(2L, "Spring Boot의 핵심 기능은?", "핵심 기능"))
                )
        );
        Interview interview = interviewRepository.findById(result.id()).orElseThrow();
        assertThat(interview.getUser().getId()).isEqualTo(user.getId());
        assertThat(interview.getGuestPassword()).isNull();
        assertThat(promptAnswerRepository.findAll()).hasSize(2);
        assertThat(dialogueRepository.findAll()).hasSize(2);
    }

    @Test
    void 게스트_인터뷰를_저장한다() {
        String guestPassword = "1234";

        Prompt prompt = new Prompt("백엔드 면접", "백엔드 관련 질문을 생성해주세요.");
        promptRepository.save(prompt);

        PromptQuery query1 = new PromptQuery("가장 많이 사용한 언어는?", prompt);
        PromptQuery query2 = new PromptQuery("가장 자신 있는 기술은?", prompt);
        promptQueryRepository.saveAll(List.of(query1, query2));

        PromptAnswerRequest answer1 = new PromptAnswerRequest(query1.getId(), "자바입니다");
        PromptAnswerRequest answer2 = new PromptAnswerRequest(query2.getId(), "정권 찌르기");
        List<PromptAnswerRequest> answers = List.of(answer1, answer2);

        DialogueRequest dialogue1 = new DialogueRequest("Java의 장점은?", "객체지향적이고 플랫폼 독립적이다.");
        DialogueRequest dialogue2 = new DialogueRequest("Spring Boot의 핵심 기능은?", "핵심 기능");
        List<DialogueRequest> dialogues = List.of(dialogue1, dialogue2);

        GuestInterviewRequest request = new GuestInterviewRequest("백엔드 면접 인터뷰", prompt.getId(), guestPassword, answers,
                dialogues);

        InterviewDto result = interviewService.saveGuestInterview(request);

        assertThat(result).isEqualTo(
                new InterviewDto(
                        1L,
                        "백엔드 면접 인터뷰",
                        List.of(
                                new PromptAnswerDto(1L, 1L, "가장 많이 사용한 언어는?", "자바입니다"),
                                new PromptAnswerDto(2L, 2L, "가장 자신 있는 기술은?", "정권 찌르기")),
                        List.of(
                                new DialogueDto(1L, "Java의 장점은?", "객체지향적이고 플랫폼 독립적이다."),
                                new DialogueDto(2L, "Spring Boot의 핵심 기능은?", "핵심 기능"))
                )
        );
        Interview interview = interviewRepository.findById(result.id()).orElseThrow();
        assertThat(interview.getUser()).isNull();
        assertThat(interview.getGuestPassword()).isEqualTo(guestPassword);
        assertThat(promptAnswerRepository.findAll()).hasSize(2);
        assertThat(dialogueRepository.findAll()).hasSize(2);
    }

    @Test
    void 존재하지_않는_프롬프트_ID로_인터뷰_저장_시_예외를_던진다() {
        Long invalidPromptId = 999L;
        GuestInterviewRequest request = new GuestInterviewRequest("백엔드 면접 인터뷰", invalidPromptId, "1234", List.of(),
                List.of());

        assertThatThrownBy(() -> interviewService.saveGuestInterview(request))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(
                        String.format(NexterviewErrorCode.PROMPT_NOT_FOUND.getMessage(), invalidPromptId));
    }

    @Test
    void 존재하지_않는_ID로_인터뷰_조회_시_예외를_던진다() {
        Long invalidInterviewId = 999L;

        assertThatThrownBy(
                () -> interviewService.findGuestInterview(invalidInterviewId, new InterviewPasswordRequest("1234")))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(
                        String.format(NexterviewErrorCode.INTERVIEW_NOT_FOUND.getMessage(), invalidInterviewId)
                );
    }

    @Test
    void userInterviewType을_조회한다() {
        Interview userInterview = interviewFixture.getSavedUserInterview();

        InterviewTypeDto result = interviewService.getInterviewType(userInterview.getId());

        assertThat(result).isEqualTo(InterviewTypeDto.of(InterviewType.USER));
    }

    @Test
    void guestInterviewType을_조회한다() {
        Interview guestInterview = interviewFixture.getSavedGuestInterview();

        InterviewTypeDto result = interviewService.getInterviewType(guestInterview.getId());

        assertThat(result).isEqualTo(InterviewTypeDto.of(InterviewType.GUEST));
    }

    @Test
    void 잘못된_인터뷰_타입으로_인터뷰를_조회하면_예외를_던진다() {
        Interview userInterview = interviewFixture.getSavedUserInterview();

        assertThatThrownBy(
                () -> interviewService.findGuestInterview(userInterview.getId(), new InterviewPasswordRequest("1234")))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.INVALID_INTERVIEW_TYPE.getMessage());
    }

    @Test
    void 인증되지_않은_유저가_유저인터뷰를_조회하면_예외를_던진다() {
        Interview userInterview = interviewFixture.getSavedUserInterview();

        assertThatThrownBy(() -> interviewService.findUserInterview(userInterview.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 다른_유저의_인터뷰를_조회하면_예외를_던진다() {
        Interview userInterview = interviewFixture.getSavedUserInterview();
        User user = userFixture.getAuthenticatedUser("user2@gmail.com", "user2", "potato!123");
        assertThatThrownBy(() -> interviewService.findUserInterview(userInterview.getId()))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.INVALID_INTERVIEW_ACCESS.getMessage());
    }

    @Test
    void 잘못된_패스워드로_게스트_인터뷰_조회하면_예외를_던진다() {
        Interview guestInterview = interviewFixture.getSavedGuestInterview();

        InterviewPasswordRequest wrongPasswordRequest = new InterviewPasswordRequest("wrongPassword");
        assertThatThrownBy(() -> interviewService.findGuestInterview(guestInterview.getId(), wrongPasswordRequest))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.INTERVIEW_GUEST_PASSWORD_MISMATCH.getMessage());
    }

    @Test
    void 유저의_인터뷰_목록을_조회한다() {
        User user = userFixture.getAuthenticatedUser("abcd@gmail.com", "potato", "potato123!");

        interviewFixture.getSavedUserInterview("인터뷰1", user);
        interviewFixture.getSavedUserInterview("인터뷰2", user);

        List<InterviewPreviewDto> result = interviewService.findUserInterviews();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(InterviewPreviewDto::title)
                .containsExactlyInAnyOrder("인터뷰1", "인터뷰2");
    }

    @Test
    void 유저_인터뷰를_수정한다() {
        User user = userFixture.getAuthenticatedUser("abc@naver.com", "potato", "password123");
        Interview interview = interviewFixture.getSavedUserInterview("기존 제목", user);

        Prompt prompt = new Prompt("백엔드 면접", "백엔드 관련 질문");
        Prompt newPrompt = new Prompt("토픽", "지시문 지시문");
        promptRepository.saveAll(List.of(prompt, newPrompt));

        PromptQuery query1 = new PromptQuery("좋아하는 언어는?", prompt);
        PromptQuery query2 = new PromptQuery("자신 있는 기술은?", prompt);
        PromptQuery query3 = new PromptQuery("좋아하는 언어는??", newPrompt);
        PromptQuery query4 = new PromptQuery("자신 있는 기술은??", newPrompt);
        promptQueryRepository.saveAll(List.of(query1, query2, query3, query4));

        PromptAnswer promptAnswer1 = new PromptAnswer("한국어", query1, interview);
        PromptAnswer promptAnswer2 = new PromptAnswer("하늘보리 원샷", query2, interview);
        promptAnswerRepository.saveAll(List.of(promptAnswer1, promptAnswer2));

        Dialogue dialogue = new Dialogue("내 이름은 코난", "탐정이죠.", interview);
        dialogueRepository.save(dialogue);

        PromptAnswerRequest newAnswer1 = new PromptAnswerRequest(query3.getId(), "파이썬");
        PromptAnswerRequest newAnswer2 = new PromptAnswerRequest(query4.getId(), "Django");
        DialogueRequest newDialogue1 = new DialogueRequest("시가 현실적이면?", "시리얼");
        DialogueRequest newDialogue2 = new DialogueRequest("섹시한 소금은?", "요염");

        UserInterviewUpdateRequest request = new UserInterviewUpdateRequest(
                interview.getId(),
                "수정된 제목",
                newPrompt.getId(),
                List.of(newAnswer1, newAnswer2),
                List.of(newDialogue1, newDialogue2)
        );

        InterviewDto result = interviewService.updateUserInterview(request);

        assertThat(result.title()).isEqualTo("수정된 제목");
        assertThat(result.promptAnswers()).extracting(PromptAnswerDto::answer)
                .containsExactly("파이썬", "Django");
        assertThat(result.dialogues()).extracting(DialogueDto::answer)
                .containsExactly("시리얼", "요염");
    }

    @Test
    void 게스트_인터뷰를_수정한다() {
        Interview interview = interviewFixture.getSavedGuestInterview("기존 제목", "1234");

        Prompt prompt = new Prompt("백엔드 면접", "백엔드 관련 질문");
        Prompt newPrompt = new Prompt("토픽", "지시문 지시문");
        promptRepository.saveAll(List.of(prompt, newPrompt));

        PromptQuery query1 = new PromptQuery("좋아하는 언어는?", prompt);
        PromptQuery query2 = new PromptQuery("자신 있는 기술은?", prompt);
        PromptQuery query3 = new PromptQuery("좋아하는 언어는??", newPrompt);
        PromptQuery query4 = new PromptQuery("자신 있는 기술은??", newPrompt);
        promptQueryRepository.saveAll(List.of(query1, query2, query3, query4));

        PromptAnswer promptAnswer1 = new PromptAnswer("한국어", query1, interview);
        PromptAnswer promptAnswer2 = new PromptAnswer("하늘보리 원샷", query2, interview);
        promptAnswerRepository.saveAll(List.of(promptAnswer1, promptAnswer2));

        Dialogue dialogue = new Dialogue("내 이름은 코난", "탐정이죠.", interview);
        dialogueRepository.save(dialogue);

        PromptAnswerRequest newAnswer1 = new PromptAnswerRequest(query3.getId(), "파이썬");
        PromptAnswerRequest newAnswer2 = new PromptAnswerRequest(query4.getId(), "Django");
        DialogueRequest newDialogue1 = new DialogueRequest("시가 현실적이면?", "시리얼");
        DialogueRequest newDialogue2 = new DialogueRequest("섹시한 소금은?", "요염");

        GuestInterviewUpdateRequest request = new GuestInterviewUpdateRequest(
                interview.getId(),
                "수정된 제목",
                newPrompt.getId(),
                "1234",
                List.of(newAnswer1, newAnswer2),
                List.of(newDialogue1, newDialogue2)
        );

        InterviewDto result = interviewService.updateGuestInterview(request);

        assertThat(result.title()).isEqualTo("수정된 제목");
        assertThat(result.promptAnswers()).extracting(PromptAnswerDto::answer)
                .containsExactly("파이썬", "Django");
        assertThat(result.dialogues()).extracting(DialogueDto::answer)
                .containsExactly("시리얼", "요염");
    }

    @Test
    void 수정_시_게스트_비밀번호가_틀리면_예외를_던진다() {
        Interview interview = interviewFixture.getSavedGuestInterview("기존 제목", "1234");
        Prompt prompt = new Prompt("백엔드 면접", "백엔드 관련 질문");
        promptRepository.save(prompt);
        PromptQuery query1 = new PromptQuery("좋아하는 언어는?", prompt);
        promptQueryRepository.save(query1);

        GuestInterviewUpdateRequest request = new GuestInterviewUpdateRequest(
                interview.getId(),
                "수정된 제목",
                prompt.getId(),
                "2345",
                List.of(new PromptAnswerRequest(query1.getId(), "파이썬")),
                List.of(new DialogueRequest("시가 현실적이면?", "시리얼"))
        );

        assertThatCode(() -> interviewService.updateGuestInterview(request))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.INTERVIEW_GUEST_PASSWORD_MISMATCH.getMessage());
    }

    @Test
    void 다른_유저의_인터뷰를_수정하면_예외를_던진다() {
        User other = userFixture.getSavedUser("abc@other.com", "other", "pass!!!!!!");
        userFixture.getAuthenticatedUser("abc@naver.com", "potato", "password123");
        Interview interview = interviewFixture.getSavedUserInterview("기존 제목", other);
        Prompt prompt = new Prompt("백엔드 면접", "백엔드 관련 질문");
        promptRepository.save(prompt);
        PromptQuery query1 = new PromptQuery("좋아하는 언어는?", prompt);
        promptQueryRepository.save(query1);

        UserInterviewUpdateRequest request = new UserInterviewUpdateRequest(
                interview.getId(),
                "수정된 제목",
                prompt.getId(),
                List.of(new PromptAnswerRequest(query1.getId(), "파이썬")),
                List.of(new DialogueRequest("시가 현실적이면?", "시리얼"))
        );

        assertThatCode(() -> interviewService.updateUserInterview(request))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.INVALID_INTERVIEW_ACCESS.getMessage());
    }

    @Test
    void 사용자_인터뷰를_삭제하면_관련_답변과_문답도_삭제된다() {
        User user = userFixture.getAuthenticatedUser("test@gmail.com", "test", "test1234!");

        Interview interview = interviewFixture.getSavedUserInterview("title", user);
        Long interviewId = interview.getId();

        Prompt prompt = new Prompt("백엔드 면접", "백엔드 관련 질문을 생성해주세요.");
        promptRepository.save(prompt);

        PromptQuery query = new PromptQuery("가장 많이 사용한 언어는?", prompt);
        promptQueryRepository.save(query);

        promptAnswerRepository.save(new PromptAnswer("answer", query, interview));
        dialogueRepository.save(new Dialogue("question", "answer", interview));

        interviewService.deleteUserInterview(interviewId);

        assertThat(interviewRepository.findById(interviewId)).isEmpty();
        assertThat(promptAnswerRepository.findAll()).isEmpty();
        assertThat(dialogueRepository.findAll()).isEmpty();
    }

    @Test
    void 게스트_인터뷰를_삭제하면_관련_답변과_문답도_삭제된다() {
        Interview interview = interviewFixture.getSavedGuestInterview();
        Long interviewId = interview.getId();
        String guestPassword = interview.getGuestPassword();

        Prompt prompt = new Prompt("백엔드 면접", "백엔드 관련 질문을 생성해주세요.");
        promptRepository.save(prompt);

        PromptQuery query = new PromptQuery("가장 많이 사용한 언어는?", prompt);
        promptQueryRepository.save(query);

        promptAnswerRepository.save(new PromptAnswer("answer", query, interview));
        dialogueRepository.save(new Dialogue("question", "answer", interview));

        interviewService.deleteGuestInterview(
                new GuestInterviewDeleteRequest(interviewId, guestPassword)
        );

        assertThat(interviewRepository.findById(interviewId)).isEmpty();
        assertThat(promptAnswerRepository.findAll()).isEmpty();
        assertThat(dialogueRepository.findAll()).isEmpty();
    }

    @Test
    void 다른_유저의_인터뷰를_삭제하면_예외를_던진다() {
        User interviewOwner = userFixture.getAuthenticatedUser("owner@gmail.com", "owner", "password1!");
        userFixture.getAuthenticatedUser("attacker@gmail.com", "attacker", "password2!");

        Interview interview = interviewFixture.getSavedUserInterview("title", interviewOwner);
        Long interviewId = interview.getId();

        assertThatThrownBy(() -> interviewService.deleteUserInterview(interviewId))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.INVALID_INTERVIEW_ACCESS.getMessage());
    }

    @Test
    void 존재하지_않는_사용자_인터뷰를_삭제하면_예외를_던진다() {
        userFixture.getAuthenticatedUser("owner@gmail.com", "owner", "password1!");
        Long invalidInterviewId = 9999L;

        assertThatThrownBy(() -> interviewService.deleteUserInterview(invalidInterviewId))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(
                        String.format(NexterviewErrorCode.INTERVIEW_NOT_FOUND.getMessage(), invalidInterviewId));
    }

    @Test
    void 잘못된_비밀번호로_게스트_인터뷰를_삭제하면_예외를_던진다() {
        Interview interview = interviewFixture.getSavedGuestInterview("title", "1234");
        Long interviewId = interview.getId();
        String wrongPassword = "2455";

        GuestInterviewDeleteRequest request = new GuestInterviewDeleteRequest(interviewId, wrongPassword);

        assertThatThrownBy(() -> interviewService.deleteGuestInterview(request))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.INTERVIEW_GUEST_PASSWORD_MISMATCH.getMessage());
    }
}
