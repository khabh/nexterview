package com.nexterview.server.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexterview.server.service.InterviewService;
import com.nexterview.server.service.dto.request.DialogueRequest;
import com.nexterview.server.service.dto.request.GuestInterviewRequest;
import com.nexterview.server.service.dto.request.InterviewPasswordRequest;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import com.nexterview.server.service.dto.request.UserInterviewRequest;
import com.nexterview.server.service.dto.response.DialogueDto;
import com.nexterview.server.service.dto.response.InterviewDto;
import com.nexterview.server.service.dto.response.InterviewPreviewDto;
import com.nexterview.server.service.dto.response.InterviewTypeDto;
import com.nexterview.server.service.dto.response.PromptAnswerDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InterviewController.class)
@Import(TestSecurityConfig.class)
class InterviewControllerTest {

    @MockitoBean
    private InterviewService interviewService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void 유저_인터뷰_저장() throws Exception {
        UserInterviewRequest request = new UserInterviewRequest(
                "백엔드 면접 인터뷰",
                1L,
                List.of(
                        new PromptAnswerRequest(1L, "자바입니다"),
                        new PromptAnswerRequest(2L, "Spring Boot")
                ),
                List.of(
                        new DialogueRequest("Java의 장점은?", "객체지향적이고 플랫폼 독립적이다."),
                        new DialogueRequest("Spring Boot의 핵심 기능은?", "자동 설정과 내장 서버 지원이다.")
                )
        );

        InterviewDto response = new InterviewDto(
                1L,
                "백엔드 면접 인터뷰",
                List.of(
                        new PromptAnswerDto(1L, 1L, "자바는 무엇인가요?", "자바입니다"),
                        new PromptAnswerDto(2L, 2L, "스프링 부트란?", "Spring Boot")
                ),
                List.of(
                        new DialogueDto(1L, "Java의 장점은?", "객체지향적이고 플랫폼 독립적이다."),
                        new DialogueDto(2L, "Spring Boot의 핵심 기능은?", "자동 설정과 내장 서버 지원이다.")
                )
        );

        when(interviewService.saveUserInterview(any())).thenReturn(response);

        mockMvc.perform(post("/api/user-interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("백엔드 면접 인터뷰"))

                .andExpect(jsonPath("$.promptAnswers.length()").value(2))
                .andExpect(jsonPath("$.promptAnswers[0].id").value(1))
                .andExpect(jsonPath("$.promptAnswers[0].promptQueryId").value(1))
                .andExpect(jsonPath("$.promptAnswers[0].query").value("자바는 무엇인가요?"))
                .andExpect(jsonPath("$.promptAnswers[0].answer").value("자바입니다"))
                .andExpect(jsonPath("$.promptAnswers[1].id").value(2))
                .andExpect(jsonPath("$.promptAnswers[1].promptQueryId").value(2))
                .andExpect(jsonPath("$.promptAnswers[1].query").value("스프링 부트란?"))
                .andExpect(jsonPath("$.promptAnswers[1].answer").value("Spring Boot"))

                .andExpect(jsonPath("$.dialogues.length()").value(2))
                .andExpect(jsonPath("$.dialogues[0].id").value(1))
                .andExpect(jsonPath("$.dialogues[0].question").value("Java의 장점은?"))
                .andExpect(jsonPath("$.dialogues[0].answer").value("객체지향적이고 플랫폼 독립적이다."))
                .andExpect(jsonPath("$.dialogues[1].id").value(2))
                .andExpect(jsonPath("$.dialogues[1].question").value("Spring Boot의 핵심 기능은?"))
                .andExpect(jsonPath("$.dialogues[1].answer").value("자동 설정과 내장 서버 지원이다."));
    }

    @Test
    void 게스트_인터뷰_저장() throws Exception {
        GuestInterviewRequest request = new GuestInterviewRequest(
                "백엔드 면접 인터뷰",
                1L,
                "1234",
                List.of(
                        new PromptAnswerRequest(1L, "자바입니다"),
                        new PromptAnswerRequest(2L, "Spring Boot")
                ),
                List.of(
                        new DialogueRequest("Java의 장점은?", "객체지향적이고 플랫폼 독립적이다."),
                        new DialogueRequest("Spring Boot의 핵심 기능은?", "자동 설정과 내장 서버 지원이다.")
                )
        );

        InterviewDto response = new InterviewDto(
                1L,
                "백엔드 면접 인터뷰",
                List.of(
                        new PromptAnswerDto(1L, 1L, "자바는 무엇인가요?", "자바입니다"),
                        new PromptAnswerDto(2L, 2L, "스프링 부트란?", "Spring Boot")
                ),
                List.of(
                        new DialogueDto(1L, "Java의 장점은?", "객체지향적이고 플랫폼 독립적이다."),
                        new DialogueDto(2L, "Spring Boot의 핵심 기능은?", "자동 설정과 내장 서버 지원이다.")
                )
        );

        when(interviewService.saveGuestInterview(any())).thenReturn(response);

        mockMvc.perform(post("/api/guest-interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("백엔드 면접 인터뷰"))

                .andExpect(jsonPath("$.promptAnswers.length()").value(2))
                .andExpect(jsonPath("$.promptAnswers[0].id").value(1))
                .andExpect(jsonPath("$.promptAnswers[0].promptQueryId").value(1))
                .andExpect(jsonPath("$.promptAnswers[0].query").value("자바는 무엇인가요?"))
                .andExpect(jsonPath("$.promptAnswers[0].answer").value("자바입니다"))
                .andExpect(jsonPath("$.promptAnswers[1].id").value(2))
                .andExpect(jsonPath("$.promptAnswers[1].promptQueryId").value(2))
                .andExpect(jsonPath("$.promptAnswers[1].query").value("스프링 부트란?"))
                .andExpect(jsonPath("$.promptAnswers[1].answer").value("Spring Boot"))

                .andExpect(jsonPath("$.dialogues.length()").value(2))
                .andExpect(jsonPath("$.dialogues[0].id").value(1))
                .andExpect(jsonPath("$.dialogues[0].question").value("Java의 장점은?"))
                .andExpect(jsonPath("$.dialogues[0].answer").value("객체지향적이고 플랫폼 독립적이다."))
                .andExpect(jsonPath("$.dialogues[1].id").value(2))
                .andExpect(jsonPath("$.dialogues[1].question").value("Spring Boot의 핵심 기능은?"))
                .andExpect(jsonPath("$.dialogues[1].answer").value("자동 설정과 내장 서버 지원이다."));
    }

    @Test
    void 인터뷰_저장_시_필수_값_없으면_오류가_발생한다() throws Exception {
        GuestInterviewRequest request = new GuestInterviewRequest(
                "",
                null,
                "",
                List.of(),
                List.of()
        );

        mockMvc.perform(post("/api/guest-interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 인터뷰_타입_조회() throws Exception {
        Long interviewId = 1L;
        InterviewTypeDto response = new InterviewTypeDto("USER");

        when(interviewService.getInterviewType(interviewId)).thenReturn(response);

        mockMvc.perform(get("/api/interviews/{interviewId}/type", interviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interviewType").value("USER"));
    }

    @Test
    @WithMockUser
    void 사용자_인터뷰_조회() throws Exception {
        Long interviewId = 1L;
        InterviewDto response = new InterviewDto(
                1L,
                "백엔드 면접 인터뷰",
                List.of(
                        new PromptAnswerDto(1L, 1L, "자바는 무엇인가요?", "자바입니다"),
                        new PromptAnswerDto(2L, 2L, "스프링 부트란?", "Spring Boot")
                ),
                List.of(
                        new DialogueDto(1L, "Java의 장점은?", "객체지향적이고 플랫폼 독립적이다."),
                        new DialogueDto(2L, "Spring Boot의 핵심 기능은?", "자동 설정과 내장 서버 지원이다.")
                )
        );

        when(interviewService.findUserInterview(interviewId)).thenReturn(response);

        mockMvc.perform(get("/api/user-interviews/{interviewId}", interviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("백엔드 면접 인터뷰"))
                .andExpect(jsonPath("$.promptAnswers.length()").value(2))
                .andExpect(jsonPath("$.promptAnswers[0].id").value(1))
                .andExpect(jsonPath("$.promptAnswers[0].query").value("자바는 무엇인가요?"))
                .andExpect(jsonPath("$.promptAnswers[0].answer").value("자바입니다"))
                .andExpect(jsonPath("$.dialogues.length()").value(2))
                .andExpect(jsonPath("$.dialogues[0].question").value("Java의 장점은?"))
                .andExpect(jsonPath("$.dialogues[0].answer").value("객체지향적이고 플랫폼 독립적이다."));
    }

    @Test
    void 게스트_인터뷰_조회() throws Exception {
        Long interviewId = 1L;
        String password = "1234";
        InterviewPasswordRequest request = new InterviewPasswordRequest(password);

        InterviewDto response = new InterviewDto(
                1L,
                "백엔드 면접 인터뷰",
                List.of(
                        new PromptAnswerDto(1L, 1L, "자바는 무엇인가요?", "자바입니다"),
                        new PromptAnswerDto(2L, 2L, "스프링 부트란?", "Spring Boot")
                ),
                List.of(
                        new DialogueDto(1L, "Java의 장점은?", "객체지향적이고 플랫폼 독립적이다."),
                        new DialogueDto(2L, "Spring Boot의 핵심 기능은?", "자동 설정과 내장 서버 지원이다.")
                )
        );

        when(interviewService.findGuestInterview(interviewId, request)).thenReturn(response);

        mockMvc.perform(post("/api/guest-interviews/{interviewId}", interviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("백엔드 면접 인터뷰"))
                .andExpect(jsonPath("$.promptAnswers.length()").value(2))
                .andExpect(jsonPath("$.dialogues.length()").value(2));
    }

    @Test
    @WithMockUser
    void 사용자_인터뷰_목록_조회() throws Exception {
        InterviewPreviewDto interview1 = new InterviewPreviewDto(
                1L,
                "자바 인터뷰",
                List.of("자바란?", "OOP의 4가지 특징?"));
        InterviewPreviewDto interview2 = new InterviewPreviewDto(
                2L,
                "네트워크 인터뷰",
                List.of("HTTP란?", "HTTP와 HTTPS 차이?")
        );

        when(interviewService.findUserInterviews()).thenReturn(List.of(interview1, interview2));

        mockMvc.perform(get("/api/user-interviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("자바 인터뷰"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("네트워크 인터뷰"));
    }
}
