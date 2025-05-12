package com.nexterview.server.controller.api;

import com.nexterview.server.service.InterviewService;
import com.nexterview.server.service.dto.request.GuestInterviewRequest;
import com.nexterview.server.service.dto.request.InterviewPasswordRequest;
import com.nexterview.server.service.dto.request.UserInterviewRequest;
import com.nexterview.server.service.dto.response.InterviewDto;
import com.nexterview.server.service.dto.response.InterviewTypeDto;
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
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/user-interviews")
    public InterviewDto saveUserInterview(@Valid @RequestBody UserInterviewRequest request) {
        return interviewService.saveUserInterview(request);
    }

    @PostMapping("/guest-interviews")
    public InterviewDto saveGuestInterview(@Valid @RequestBody GuestInterviewRequest request) {
        return interviewService.saveGuestInterview(request);
    }

    @PostMapping("/guest-interviews/{interviewId}")
    public InterviewDto getGuestInterview(
            @PathVariable Long interviewId, @Valid @RequestBody InterviewPasswordRequest request
    ) {
        return interviewService.findGuestInterview(interviewId, request);
    }

    @GetMapping("/interviews/{interviewId}/type")
    public InterviewTypeDto getInterviewType(@PathVariable Long interviewId) {
        return interviewService.getInterviewType(interviewId);
    }

    @GetMapping("/user-interviews/{interviewId}")
    public InterviewDto getUserInterview(@PathVariable Long interviewId) {
        return interviewService.findUserInterview(interviewId);
    }

    @GetMapping("/user-interviews")
    public List<InterviewDto> getUserInterviews() {
        return interviewService.findUserInterviews();
    }
}
