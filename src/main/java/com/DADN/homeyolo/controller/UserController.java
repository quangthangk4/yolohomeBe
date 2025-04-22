package com.DADN.homeyolo.controller;

import com.DADN.homeyolo.dto.ApiResponse;
import com.DADN.homeyolo.dto.request.UserCreationRequest;
import com.DADN.homeyolo.dto.request.UserLoginRequest;
import com.DADN.homeyolo.dto.response.UserResponse;
import com.DADN.homeyolo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ApiResponse<UserResponse> login(@RequestBody UserLoginRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.login(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }


}
