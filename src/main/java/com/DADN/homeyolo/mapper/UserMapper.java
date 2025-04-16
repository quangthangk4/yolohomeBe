package com.DADN.homeyolo.mapper;

import com.DADN.homeyolo.dto.request.UserCreationRequest;
import com.DADN.homeyolo.dto.response.UserResponse;
import com.DADN.homeyolo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
}
