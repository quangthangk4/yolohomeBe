package com.DADN.homeyolo.controller;

import com.DADN.homeyolo.dto.ApiResponse;
import com.DADN.homeyolo.dto.request.DoorCreatePasswordRequest;
import com.DADN.homeyolo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/door")
@RequiredArgsConstructor
public class DoorController {

    private final UserService userService;

    @PostMapping("/create-password")
    public ApiResponse<Void> createPasswordDoor(@RequestBody DoorCreatePasswordRequest request) {
        userService.createPasswordDoor(request);
        return ApiResponse.<Void>builder()
                .message("Door password has been created successfully")
                .build();
    }

    @PutMapping("/update-password")
    public ApiResponse<Void> updatePasswordDoor(@RequestBody DoorCreatePasswordRequest request) {
        userService.ChangePasswordDoor(request);
        return ApiResponse.<Void>builder()
                .message("Door password has been updated successfully")
                .build();
    }

    @PostMapping("/unlock")
    public ApiResponse<Void> unlockDoor(@RequestBody DoorCreatePasswordRequest request) {
        userService.unlockDoor(request.getPassword());
        return ApiResponse.<Void>builder()
                .message("Door has been unlocked successfully")
                .build();
    }

    @PostMapping("/lock")
    public ApiResponse<Void> lockDoor() {
        userService.lockDoor();
        return ApiResponse.<Void>builder()
                .message("Door has been locked successfully")
                .build();
    }

    @GetMapping("/status")
    public ApiResponse<Boolean> getDoorStatus() {
        Boolean status = userService.stateDoor();
        return ApiResponse.<Boolean>builder()
                .message("Door status retrieved successfully")
                .result(status)
                .build();
    }
}
