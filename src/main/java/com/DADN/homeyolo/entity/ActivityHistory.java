package com.DADN.homeyolo.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(value = "activity_history")
public class ActivityHistory {
    @MongoId
    private String id;
    private String username;
    private LocalDateTime time;
    private String message;
}
