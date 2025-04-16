package com.DADN.homeyolo.controller;

import com.DADN.homeyolo.dto.ApiResponse;
import com.DADN.homeyolo.dto.response.ActivityHistoryResponse;
import com.DADN.homeyolo.service.ActivityHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class ActivityHistoryController {

    private final ActivityHistoryService activityHistoryService;

    @GetMapping
    public ApiResponse<List<ActivityHistoryResponse>> history(){
        return ApiResponse.<List<ActivityHistoryResponse>>builder()
                .result(activityHistoryService.activityHistory())
                .build();
    }
}
