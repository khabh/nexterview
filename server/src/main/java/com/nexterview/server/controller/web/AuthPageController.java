package com.nexterview.server.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {

    @GetMapping("/auth")
    public String showInterviewCreatePage() {
        return "auth";
    }
}
