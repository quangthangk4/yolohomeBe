package com.DADN.homeyolo.controller;

import com.DADN.homeyolo.dto.ApiResponse;
import com.DADN.homeyolo.service.AdafruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/adafruit")
public class AdafruitController {

    private final AdafruitService adafruitService;

    @PostMapping("/{device}")
    public ApiResponse<String> controlDevice(
            @PathVariable("device") String device,
            @RequestParam String value,
            @RequestParam String username) {
        adafruitService.sendControlCommand(device, value, username);
        String message = "";
        if (device.equals("light")) {
            String message1 = value.equals("1")? "has been turned on" : "has been turned off";
            message = String.format("%s %s", device, message1);
        }
        else {
            message = String.format("%s adjusted to %s%s", device, value,"%");
        }


        return ApiResponse.<String>builder()
                .message(message)
                .build();
    }
}
