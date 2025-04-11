package com.nexterview.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.nexterview.server.domain.CustomizedPrompt;
import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptComponent;
import com.nexterview.server.domain.PromptQuery;
import com.nexterview.server.repository.PromptQueryRepository;
import com.nexterview.server.repository.PromptRepository;
import com.nexterview.server.service.dto.request.GenerateDialoguesRequest;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import com.nexterview.server.service.dto.response.GeneratedDialogueDto;
import com.nexterview.server.service.dto.response.PromptDto;
import com.nexterview.server.util.DatabaseCleaner;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PromptServiceTest {

    @Autowired
    private PromptService promptService;

    @Autowired
    private PromptRepository promptRepository;

    @Autowired
    private PromptQueryRepository promptQueryRepository;

    @MockitoBean
    private DialogueGenerator dialogueGenerator;

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

    @Test
    void 프롬프트_기반_대화를_생성한다() {
        Prompt prompt = new Prompt("백엔드 개발자 면접", "지원자 정보를 참고해서 인터뷰 질문을 두 개 생성해줘");
        promptRepository.save(prompt);

        PromptQuery query1 = new PromptQuery("가장 많이 사용한 프로그래밍 언어", prompt);
        PromptQuery query2 = new PromptQuery("가장 많이 사용한 프레임워크", prompt);
        promptQueryRepository.saveAll(List.of(query1, query2));

        PromptAnswerRequest answer1 = new PromptAnswerRequest(query1.getId(), "자바");
        PromptAnswerRequest answer2 = new PromptAnswerRequest(query2.getId(), "Spring");
        List<PromptAnswerRequest> answers = List.of(answer1, answer2);

        GenerateDialoguesRequest request = new GenerateDialoguesRequest(prompt.getId(), answers);

        CustomizedPrompt expectedCustomizedPrompt = new CustomizedPrompt(
                prompt.getInstruction(),
                List.of(new PromptComponent(query1.getQuery(), answer1.answer()),
                        new PromptComponent(query2.getQuery(), answer2.answer())
                ));
        List<GeneratedDialogueDto> generatedDialogues = List.of(
                new GeneratedDialogueDto("Spring에서 Bean의 라이프사이클을 설명해주세요.",
                        "Spring의 빈(Bean) 라이프사이클은 객체가 생성되고, 사용되며, 소멸되는 과정을 의미합니다."),
                new GeneratedDialogueDto("equals()와 hashCode()는 왜 같이 오버라이딩해야 하나요?",
                        "equals()와 hashCode()는 객체의 동등성을 비교할 때 함께 사용되기 때문입니다.")
        );
        when(dialogueGenerator.generate(expectedCustomizedPrompt)).thenReturn(
                new GeneratedDialogues(0, generatedDialogues));

        List<GeneratedDialogueDto> result = promptService.generateDialogues(request);

        assertThat(result).isEqualTo(generatedDialogues);
    }
}
