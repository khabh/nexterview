package com.nexterview.server.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InterviewPageController {

    @GetMapping("/interview")
    public String showInterviewPage() {
        return "interview";
    }
}
