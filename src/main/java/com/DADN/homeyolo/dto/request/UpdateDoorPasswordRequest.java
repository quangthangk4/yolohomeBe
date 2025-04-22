package com.DADN.homeyolo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDoorPasswordRequest {
    private String currentPassword;
    private String newPassword;
}