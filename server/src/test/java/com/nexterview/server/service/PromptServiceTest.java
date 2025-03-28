package com.nexterview.server.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptQuery;
import com.nexterview.server.repository.PromptQueryRepository;
import com.nexterview.server.repository.PromptRepository;
import com.nexterview.server.service.dto.response.PromptDto;
import com.nexterview.server.util.DatabaseCleaner;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PromptServiceTest {

    @Autowired
    private PromptService promptService;

    @Autowired
    private PromptRepository promptRepository;

    @Autowired
    private PromptQueryRepository promptQueryRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
    }

    @Test
    void 프롬프트_목록을_조회한다() {
        Prompt prompt1 = new Prompt("Topic1", "Instruction1");
        Prompt prompt2 = new Prompt("Topic2", "Instruction2");
        promptRepository.saveAll(List.of(prompt1, prompt2));

        PromptQuery query1 = new PromptQuery("Query1", prompt1);
        PromptQuery query2 = new PromptQuery("Query2", prompt1);
        promptQueryRepository.saveAll(List.of(query1, query2));

        List<PromptDto> expected = List.of(
                PromptDto.of(prompt1, List.of(query1, query2)),
                PromptDto.of(prompt2, List.of())
        );

        List<PromptDto> result = promptService.findAll();

        assertThat(result).isEqualTo(expected);
    }
}
