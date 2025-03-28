package com.nexterview.server.controller.api;

import com.nexterview.server.service.PromptService;
import com.nexterview.server.service.dto.response.PromptDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    @GetMapping("/prompts")
    public List<PromptDto> findAll() {
        return promptService.findAll();
    }
}
