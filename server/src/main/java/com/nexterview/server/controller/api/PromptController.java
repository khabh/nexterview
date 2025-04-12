package com.nexterview.server.controller.api;

import com.nexterview.server.controller.api.dto.request.ApiPromptAnswersRequest;
import com.nexterview.server.service.PromptService;
import com.nexterview.server.service.dto.request.GenerateDialoguesRequest;
import com.nexterview.server.service.dto.response.GeneratedDialogueDto;
import com.nexterview.server.service.dto.response.PromptDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PromptController {

    private final IpExtractor ipExtractor;
    private final PromptService promptService;

    @GetMapping("/prompts")
    public List<PromptDto> findAll() {
        return promptService.findAll();
    }

    @PostMapping("/prompts/{promptId}/dialogues")
    public List<GeneratedDialogueDto> generateDialogues(
            @PathVariable Long promptId, @RequestBody @Valid ApiPromptAnswersRequest apiRequest,
            HttpServletRequest servletRequest
    ) {
        String clientIp = ipExtractor.extract(servletRequest);
        GenerateDialoguesRequest request = new GenerateDialoguesRequest(promptId, apiRequest.promptAnswers());

        return promptService.generateDialogues(request, clientIp);
    }
}
