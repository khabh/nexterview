package com.nexterview.server.controller.api;

import com.nexterview.server.service.UserService;
import com.nexterview.server.service.dto.request.UserRequest;
import com.nexterview.server.service.dto.response.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public UserDto signup(@RequestBody UserRequest request) {
        return userService.saveUser(request);
    }
}
