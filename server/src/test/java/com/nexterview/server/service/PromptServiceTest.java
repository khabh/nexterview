package com.nexterview.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexterview.server.domain.CustomizedPrompt;
import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptComponent;
import com.nexterview.server.domain.PromptQuery;
import com.nexterview.server.domain.TokenQuota;
import com.nexterview.server.domain.User;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.PromptQueryRepository;
import com.nexterview.server.repository.PromptRepository;
import com.nexterview.server.repository.TokenQuotaRepository;
import com.nexterview.server.service.dto.request.GenerateDialoguesRequest;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import com.nexterview.server.service.dto.response.GeneratedDialogueDto;
import com.nexterview.server.service.dto.response.PromptDto;
import com.nexterview.server.util.DatabaseCleaner;
import com.nexterview.server.util.UserFixture;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    @Autowired
    private TokenQuotaRepository tokenQuotaRepository;

    @MockitoBean
    private DialogueGenerator dialogueGenerator;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserFixture userFixture;

    private final String ip = "127.0.0.1";
    private final String lockKey = "prompt:access:ip:lock:" + ip;
    private final String accessKey = "prompt:access:ip:" + ip;

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
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
    void 유저가_질문을_생성한다() {
        Prompt prompt = new Prompt("백엔드 면접", "답변을 참고해서 질문 2개 생성해줘");
        promptRepository.save(prompt);

        PromptQuery query1 = new PromptQuery("가장 자신 있는 기술", prompt);
        PromptQuery query2 = new PromptQuery("협업 시 중요하게 여기는 점", prompt);
        promptQueryRepository.saveAll(List.of(query1, query2));

        PromptAnswerRequest answer1 = new PromptAnswerRequest(query1.getId(), "Java");
        PromptAnswerRequest answer2 = new PromptAnswerRequest(query2.getId(), "소통");
        GenerateDialoguesRequest request = new GenerateDialoguesRequest(prompt.getId(), List.of(answer1, answer2));

        User user = userFixture.getAuthenticatedUser("user@example.com", "nickname", "12345678");
        TokenQuota tokenQuota = new TokenQuota(user, 10);
        tokenQuotaRepository.save(tokenQuota);

        List<GeneratedDialogueDto> dialogues = List.of(
                new GeneratedDialogueDto("Java의 장점은?", "객체 지향, 풍부한 생태계 등입니다."),
                new GeneratedDialogueDto("소통이 중요한 이유는?", "협업 과정에서 오해를 줄이기 때문입니다.")
        );
        when(dialogueGenerator.generate(any())).thenReturn(new GeneratedDialogues(10, dialogues));

        List<GeneratedDialogueDto> result = promptService.generateDialoguesForUser(request);

        assertThat(result).isEqualTo(dialogues);
        TokenQuota updatedQuota = tokenQuotaRepository.findByUser(user).orElseThrow();
        assertThat(updatedQuota.getRemainingQuota()).isEqualTo(0);
    }

    @Test
    void 유저에게_토큰_쿼터가_없으면_자동_생성된다() {
        Prompt prompt = promptRepository.save(new Prompt("CS 질문", "답변 참고해서 질문 1개 생성"));
        PromptQuery query = promptQueryRepository.save(new PromptQuery("관심 있는 기술", prompt));

        User user = userFixture.getAuthenticatedUser("user@example.com", "nickname", "12345678");

        PromptAnswerRequest answer = new PromptAnswerRequest(query.getId(), "Java");
        GenerateDialoguesRequest request = new GenerateDialoguesRequest(prompt.getId(), List.of(answer));
        List<GeneratedDialogueDto> dialogues = List.of(
                new GeneratedDialogueDto("Java의 장점은?", "객체 지향, 풍부한 생태계 등입니다."),
                new GeneratedDialogueDto("소통이 중요한 이유는?", "협업 과정에서 오해를 줄이기 때문입니다.")
        );
        when(dialogueGenerator.generate(any())).thenReturn(new GeneratedDialogues(10, dialogues));

        promptService.generateDialoguesForUser(request);

        TokenQuota quota = tokenQuotaRepository.findByUser(user).orElse(null);
        assertThat(quota).isNotNull();
        assertThat(quota.getRemainingQuota()).isEqualTo(222_212);
    }

    @Test
    void 토큰이_부족하면_질문을_생성하지_않고_예외가_발생한다() {
        Prompt prompt = promptRepository.save(new Prompt("CS 질문", "답변 참고해서 질문 1개 생성"));
        PromptQuery query = promptQueryRepository.save(new PromptQuery("관심 있는 기술", prompt));

        User user = userFixture.getAuthenticatedUser("user@example.com", "nickname", "12345678");

        TokenQuota quota = new TokenQuota(user, 0);
        tokenQuotaRepository.save(quota);

        PromptAnswerRequest answer = new PromptAnswerRequest(query.getId(), "Java");
        GenerateDialoguesRequest request = new GenerateDialoguesRequest(prompt.getId(), List.of(answer));

        assertThatThrownBy(() -> promptService.generateDialoguesForUser(request))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.USER_PROMPT_ACCESS_EXCEEDED.getMessage());
        verify(dialogueGenerator, never()).generate(any());
    }

    @Test
    void 게스트가_프롬프트_기반_대화를_생성한다() {
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

        List<GeneratedDialogueDto> result = promptService.generateDialoguesForGuest(request, ip);

        assertThat(result).isEqualTo(generatedDialogues);
        assertThat(redisTemplate.hasKey(accessKey)).isTrue();
        assertThat(redisTemplate.hasKey(lockKey)).isFalse();
    }

    @Test
    void 질문_생성_실패_시_예외가_발생한다() {
        Prompt prompt = new Prompt("백엔드 개발자 면접", "지원자 정보를 참고해서 인터뷰 질문을 두 개 생성해줘");
        promptRepository.save(prompt);

        PromptQuery query1 = new PromptQuery("가장 많이 사용한 프로그래밍 언어", prompt);
        PromptQuery query2 = new PromptQuery("가장 많이 사용한 프레임워크", prompt);
        promptQueryRepository.saveAll(List.of(query1, query2));

        PromptAnswerRequest answer1 = new PromptAnswerRequest(query1.getId(), "자바");
        PromptAnswerRequest answer2 = new PromptAnswerRequest(query2.getId(), "Spring");
        List<PromptAnswerRequest> answers = List.of(answer1, answer2);

        GenerateDialoguesRequest request = new GenerateDialoguesRequest(prompt.getId(), answers);
        when(dialogueGenerator.generate(any())).thenThrow(
                new NexterviewException(NexterviewErrorCode.CHAT_API_UNAVAILABLE));

        assertThatThrownBy(() -> promptService.generateDialoguesForGuest(request, ip))
                .isInstanceOf(NexterviewException.class)
                .hasMessage(NexterviewErrorCode.CHAT_API_UNAVAILABLE.getMessage());
        assertThat(redisTemplate.hasKey(accessKey)).isFalse();
        assertThat(redisTemplate.hasKey(lockKey)).isFalse();
    }

    @Test
    void 동시에_여러_요청이_들어오면_하나만_성공한다() throws InterruptedException {
        Prompt prompt = new Prompt("제목", "지시문입니다");
        promptRepository.save(prompt);

        PromptQuery query = new PromptQuery("가장 많이 사용한 언어는?", prompt);
        promptQueryRepository.save(query);

        PromptAnswerRequest answer = new PromptAnswerRequest(query.getId(), "Java");
        GenerateDialoguesRequest request = new GenerateDialoguesRequest(prompt.getId(), List.of(answer));

        List<GeneratedDialogueDto> generatedDialogues = List.of(
                new GeneratedDialogueDto("query1", "answer1"),
                new GeneratedDialogueDto("query2", "answer2")
        );
        when(dialogueGenerator.generate(any())).thenReturn(
                new GeneratedDialogues(0, generatedDialogues));

        int threadCount = 5;

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger lockFailCount = new AtomicInteger(0);

        try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
            for (int i = 0; i < threadCount; i++) {
                executorService.execute(() -> {
                    awaitLatch(startLatch);
                    try {
                        promptService.generateDialoguesForGuest(request, ip);
                        successCount.incrementAndGet();
                    } catch (NexterviewException e) {
                        if (e.getErrorCode() == NexterviewErrorCode.REQUEST_TEMPORARILY_LOCKED) {
                            lockFailCount.incrementAndGet();
                        }
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            if (!doneLatch.await(15, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                throw new RuntimeException("Done latch await timeout");
            }

            assertThat(successCount.get()).isEqualTo(1);
            assertThat(lockFailCount.get()).isEqualTo(threadCount - 1);
            assertThat(redisTemplate.hasKey(accessKey)).isTrue();
            assertThat(redisTemplate.hasKey(lockKey)).isFalse();
        }
    }

    private static void awaitLatch(CountDownLatch startLatch) {
        try {
            startLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
