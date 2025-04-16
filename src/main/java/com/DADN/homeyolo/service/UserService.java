package com.DADN.homeyolo.service;

import com.DADN.homeyolo.dto.request.UserCreationRequest;
import com.DADN.homeyolo.dto.request.UserLoginRequest;
import com.DADN.homeyolo.dto.response.UserResponse;
import com.DADN.homeyolo.entity.User;
import com.DADN.homeyolo.exception.AppException;
import com.DADN.homeyolo.exception.ErrorCode;
import com.DADN.homeyolo.mapper.UserMapper;
import com.DADN.homeyolo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }


    public boolean login(UserLoginRequest request){
        User user = userRepository.findUserByEmail(request.getEmail()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!matches) throw new AppException(ErrorCode.INCORRECT_PASSWORD);

        return true;
    }
}
