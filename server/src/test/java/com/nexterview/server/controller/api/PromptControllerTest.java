package com.nexterview.server.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexterview.server.controller.api.dto.request.ApiPromptAnswersRequest;
import com.nexterview.server.service.AuthenticatedUserContext;
import com.nexterview.server.service.PromptService;
import com.nexterview.server.service.dto.request.GenerateDialoguesRequest;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import com.nexterview.server.service.dto.response.GeneratedDialogueDto;
import com.nexterview.server.service.dto.response.PromptDto;
import com.nexterview.server.service.dto.response.PromptQueryDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PromptController.class)
@Import(TestSecurityConfig.class)
class PromptControllerTest {

    @MockitoBean
    private PromptService promptService;

    @MockitoBean
    private IpExtractor ipExtractor;

    @MockitoBean
    private AuthenticatedUserContext authenticatedUserContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 프롬프트_목록을_조회한다() throws Exception {
        PromptDto prompt1 = new PromptDto(1L, "Topic1", "Instruction1", List.of(new PromptQueryDto(1L, "Query1")));
        PromptDto prompt2 = new PromptDto(2L, "Topic2", "Instruction2", List.of());
        when(promptService.findAll()).thenReturn(List.of(prompt1, prompt2));

        mockMvc.perform(get("/api/prompts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].topic").value("Topic1"))
                .andExpect(jsonPath("$[0].instruction").value("Instruction1"))
                .andExpect(jsonPath("$[0].queries[0].id").value(1))
                .andExpect(jsonPath("$[0].queries[0].query").value("Query1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].topic").value("Topic2"))
                .andExpect(jsonPath("$[1].instruction").value("Instruction2"));
    }

    @Test
    void 프롬프트에_대한_문답_생성_인증된_사용자() throws Exception {
        Long promptId = 1L;
        List<PromptAnswerRequest> answers = List.of(
                new PromptAnswerRequest(1L, "Answer 1"),
                new PromptAnswerRequest(2L, "Answer 2")
        );
        ApiPromptAnswersRequest request = new ApiPromptAnswersRequest(answers);
        List<GeneratedDialogueDto> generatedDialogues = List.of(
                new GeneratedDialogueDto("Generated Question 1", "Generated Answer 1"),
                new GeneratedDialogueDto("Generated Question 2", "Generated Answer 2")
        );

        when(authenticatedUserContext.isAuthenticated()).thenReturn(true);
        when(promptService.generateDialoguesForUser(any(GenerateDialoguesRequest.class)))
                .thenReturn(generatedDialogues);

        mockMvc.perform(post("/api/prompts/{promptId}/dialogues", promptId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].question").value("Generated Question 1"))
                .andExpect(jsonPath("$[0].answer").value("Generated Answer 1"))
                .andExpect(jsonPath("$[1].question").value("Generated Question 2"))
                .andExpect(jsonPath("$[1].answer").value("Generated Answer 2"));

        verify(promptService).generateDialoguesForUser(any(GenerateDialoguesRequest.class));
    }

    @Test
    void 프롬프트에_대한_문답_생성_비인증된_사용자() throws Exception {
        Long promptId = 1L;
        List<PromptAnswerRequest> answers = List.of(
                new PromptAnswerRequest(1L, "Answer 1"),
                new PromptAnswerRequest(2L, "Answer 2")
        );
        ApiPromptAnswersRequest request = new ApiPromptAnswersRequest(answers);
        List<GeneratedDialogueDto> generatedDialogues = List.of(
                new GeneratedDialogueDto("Generated Question 1", "Generated Answer 1"),
                new GeneratedDialogueDto("Generated Question 2", "Generated Answer 2")
        );

        when(authenticatedUserContext.isAuthenticated()).thenReturn(false);
        when(ipExtractor.extract(any(HttpServletRequest.class))).thenReturn("127.0.0.1");
        when(promptService.generateDialoguesForGuest(any(GenerateDialoguesRequest.class), any(String.class)))
                .thenReturn(generatedDialogues);

        mockMvc.perform(post("/api/prompts/{promptId}/dialogues", promptId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].question").value("Generated Question 1"))
                .andExpect(jsonPath("$[0].answer").value("Generated Answer 1"))
                .andExpect(jsonPath("$[1].question").value("Generated Question 2"))
                .andExpect(jsonPath("$[1].answer").value("Generated Answer 2"));

        verify(promptService).generateDialoguesForGuest(any(GenerateDialoguesRequest.class), any(String.class));
    }
}
