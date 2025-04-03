package com.nexterview.server.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class InterviewPageController {

    @GetMapping("/interview/create")
    public String showInterviewCreatePage() {
        return "interview-create";
    }

    @GetMapping("/interviews/{interviewId}")
    public String showInterviewPage(@PathVariable Long interviewId, Model model) {
        model.addAttribute("interviewId", interviewId);

        return "interview-view";
    }
}
