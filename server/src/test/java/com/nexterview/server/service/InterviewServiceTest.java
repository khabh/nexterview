package com.nexterview.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.domain.Dialogue;
import com.nexterview.server.domain.Interview;
import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptAnswer;
import com.nexterview.server.domain.PromptQuery;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.DialogueRepository;
import com.nexterview.server.repository.InterviewRepository;
import com.nexterview.server.repository.PromptAnswerRepository;
import com.nexterview.server.repository.PromptQueryRepository;
import com.nexterview.server.repository.PromptRepository;
import com.nexterview.server.service.dto.request.DialogueRequest;
import com.nexterview.server.service.dto.request.InterviewRequest;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import com.nexterview.server.service.dto.response.DialogueDto;
import com.nexterview.server.service.dto.response.InterviewDto;
import com.nexterview.server.service.dto.response.PromptAnswerDto;
import com.nexterview.server.util.DatabaseCleaner;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

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

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
    }

    @Test
    void 인터뷰를_저장한다() {
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

        InterviewRequest request = new InterviewRequest("백엔드 면접 인터뷰", prompt.getId(), answers, dialogues);

        InterviewDto result = interviewService.saveInterview(request);

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
        assertThat(interviewRepository.findAll()).hasSize(1);
        assertThat(promptAnswerRepository.findAll()).hasSize(2);
        assertThat(dialogueRepository.findAll()).hasSize(2);
    }

    @Test
    void 존재하지_않는_프롬프트_ID로_인터뷰_저장_시_예외를_던진다() {
        Long invalidPromptId = 999L;
        InterviewRequest request = new InterviewRequest("백엔드 면접 인터뷰", invalidPromptId, List.of(), List.of());

        assertThatThrownBy(() -> interviewService.saveInterview(request))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(
                        String.format(NexterviewErrorCode.PROMPT_NOT_FOUND.getMessage(), invalidPromptId));
    }

    @Test
    void 인터뷰를_ID로_조회한다() {
        Prompt prompt = new Prompt("백엔드 면접", "백엔드 관련 질문을 생성해주세요.");
        promptRepository.save(prompt);

        PromptQuery query1 = new PromptQuery("가장 많이 사용한 언어는?", prompt);
        PromptQuery query2 = new PromptQuery("가장 자신 있는 기술은?", prompt);
        promptQueryRepository.saveAll(List.of(query1, query2));

        Interview interview = new Interview("백엔드 면접 인터뷰");
        interviewRepository.save(interview);

        PromptAnswer answer1 = new PromptAnswer("자바입니다", query1, interview);
        PromptAnswer answer2 = new PromptAnswer("Spring Boot", query2, interview);
        List<PromptAnswer> answers = List.of(answer1, answer2);
        promptAnswerRepository.saveAll(answers);

        Dialogue dialogue1 = new Dialogue("Java의 장점은?", "객체지향적이고 플랫폼 독립적이다.", interview);
        Dialogue dialogue2 = new Dialogue("Spring Boot의 핵심 기능은?", "자동 설정과 내장 서버 지원이다.", interview);
        List<Dialogue> dialogues = List.of(dialogue1, dialogue2);
        dialogueRepository.saveAll(dialogues);

        InterviewDto result = interviewService.findById(interview.getId());

        assertThat(result).isEqualTo(
                new InterviewDto(
                        interview.getId(),
                        "백엔드 면접 인터뷰",
                        List.of(
                                new PromptAnswerDto(answer1.getId(), query1.getId(), "가장 많이 사용한 언어는?", "자바입니다"),
                                new PromptAnswerDto(answer2.getId(), query2.getId(), "가장 자신 있는 기술은?", "Spring Boot")
                        ),
                        List.of(
                                new DialogueDto(dialogue1.getId(), "Java의 장점은?", "객체지향적이고 플랫폼 독립적이다."),
                                new DialogueDto(dialogue2.getId(), "Spring Boot의 핵심 기능은?", "자동 설정과 내장 서버 지원이다.")
                        )
                )
        );
    }

    @Test
    void 존재하지_않는_ID로_인터뷰_조회_시_예외를_던진다() {
        Long invalidInterviewId = 999L;

        assertThatThrownBy(() -> interviewService.findById(invalidInterviewId))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(
                        String.format(NexterviewErrorCode.INTERVIEW_NOT_FOUND.getMessage(), invalidInterviewId)
                );
    }
}
