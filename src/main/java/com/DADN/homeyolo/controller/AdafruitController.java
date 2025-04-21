package com.DADN.homeyolo.controller;

import com.DADN.homeyolo.dto.ApiResponse;
import com.DADN.homeyolo.dto.response.DataAdafruitResponse;
import com.DADN.homeyolo.dto.response.TempChartResponse;
import com.DADN.homeyolo.service.AdafruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/data")
    public ApiResponse<DataAdafruitResponse> getData() {
        return ApiResponse.<DataAdafruitResponse>builder()
                .result(adafruitService.getLatestLightValue())
                .build();
    }

    @GetMapping("/temp-chart/{key_feed}/{hours}")
    public ApiResponse<List<TempChartResponse>> tempChart(
            @PathVariable("key_feed") String key_feed,
            @PathVariable("hours") String hours) {
        return ApiResponse.<List<TempChartResponse>>builder()
                .result(adafruitService.tempChart(hours,key_feed))
                .build();
    }
}
