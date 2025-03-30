package com.nexterview.server.controller.api;

import com.nexterview.server.controller.api.dto.request.ApiInterviewRequest;
import com.nexterview.server.service.InterviewService;
import com.nexterview.server.service.dto.request.InterviewRequest;
import com.nexterview.server.service.dto.response.InterviewDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/prompts/{promptId}/interviews")
    public InterviewDto saveInterview(@PathVariable Long promptId, @Valid @RequestBody ApiInterviewRequest apiRequest) {
        InterviewRequest interviewRequest = new InterviewRequest(apiRequest.title(), promptId,
                apiRequest.promptAnswers(), apiRequest.dialogues());

        return interviewService.saveInterview(interviewRequest);
    }
}
