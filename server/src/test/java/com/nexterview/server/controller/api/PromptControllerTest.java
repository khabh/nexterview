package com.nexterview.server.controller.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexterview.server.service.PromptService;
import com.nexterview.server.service.dto.response.PromptDto;
import com.nexterview.server.service.dto.response.PromptQueryDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PromptController.class)
class PromptControllerTest {

    @MockitoBean
    private PromptService promptService;

    @Autowired
    private MockMvc mockMvc;

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
}
