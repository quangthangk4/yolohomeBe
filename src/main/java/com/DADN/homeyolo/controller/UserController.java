package com.DADN.homeyolo.controller;

import com.DADN.homeyolo.dto.ApiResponse;
import com.DADN.homeyolo.dto.request.UserCreationRequest;
import com.DADN.homeyolo.dto.request.UserLoginRequest;
import com.DADN.homeyolo.dto.response.UserResponse;
import com.DADN.homeyolo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/registration")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<Boolean> login(@RequestBody UserLoginRequest request) {
        return ApiResponse.<Boolean>builder()
                .result(userService.login(request))
                .build();
    }
}
