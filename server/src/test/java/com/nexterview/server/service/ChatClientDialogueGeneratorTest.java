package com.nexterview.server.service;

import com.nexterview.server.domain.CustomizedPrompt;
import com.nexterview.server.domain.PromptComponent;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatClientDialogueGeneratorTest {

    @Autowired
    private ChatClientDialogueGenerator generator;

    @Test
    @Disabled("외부 OpenAI API 호출")
    void generate_실제_ChatClient_호출해서_생성_확인() {
        String instruction = "당신은 개발자 면접관입니다. ";
        List<PromptComponent> components = List.of(
                new PromptComponent("협업 경험", "팀 프로젝트에서 Git Flow 전략을 적용했습니다."),
                new PromptComponent("가장 어려웠던 점", "타 팀과의 의사소통 문제를 해결했습니다.")
        );
        CustomizedPrompt customizedPrompt = new CustomizedPrompt(instruction, components);

        GeneratedDialogues result = generator.generate(customizedPrompt);
        System.out.println("result = " + result);
    }
}
