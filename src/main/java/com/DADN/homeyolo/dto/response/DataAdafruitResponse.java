package com.DADN.homeyolo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataAdafruitResponse {
    private Boolean isOn;
    private String minLight;
    private String brightness;
    private String fanSpeed;
}
