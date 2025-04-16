package com.DADN.homeyolo.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@Builder
@Document(value = "user")
public class User {
    @MongoId
    private String id;
    private String username;
    private String password;
    private String email;
}
