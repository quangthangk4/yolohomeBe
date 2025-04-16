package com.DADN.homeyolo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityHistoryResponse {
    private String id;
    private String username;
    private LocalDateTime time;
    private String message;
}
