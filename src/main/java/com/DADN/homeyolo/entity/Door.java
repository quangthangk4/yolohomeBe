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
@Document(value = "door")
public class Door {
    @MongoId
    private String id;
    private String password;
    private Boolean unlock;
    private LocalDateTime created;
}
