package com.DADN.homeyolo.service;

import com.DADN.homeyolo.dto.request.DoorCreatePasswordRequest;
import com.DADN.homeyolo.dto.request.UpdateDoorPasswordRequest;
import com.DADN.homeyolo.dto.request.UserCreationRequest;
import com.DADN.homeyolo.dto.request.UserLoginRequest;
import com.DADN.homeyolo.dto.response.UserResponse;
import com.DADN.homeyolo.entity.Door;
import com.DADN.homeyolo.entity.User;
import com.DADN.homeyolo.exception.AppException;
import com.DADN.homeyolo.exception.ErrorCode;
import com.DADN.homeyolo.mapper.UserMapper;
import com.DADN.homeyolo.repository.DoorRepository;
import com.DADN.homeyolo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final DoorRepository doorRepository;

    @NonFinal
    private final String doorId = "6804b5e1f0c66f0b7e9ff70d";

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    public UserResponse login(UserLoginRequest request){
        User user = userRepository.findUserByEmail(request.getEmail()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!matches) throw new AppException(ErrorCode.INCORRECT_PASSWORD);

        return userMapper.toUserResponse(user);
    }

    public void createPasswordDoor(DoorCreatePasswordRequest request){
        if (request.getPassword().isEmpty()) throw new AppException(ErrorCode.EMPTY_PASSWORD);
        doorRepository.save(Door.builder()
                        .password(passwordEncoder.encode(request.getPassword()))
                        .unlock(false)
                        .created(LocalDateTime.now())
                .build());
    }

    public void unlockDoor(String password){
        Door door = doorRepository.findById(doorId).orElseThrow(
                () -> new AppException(ErrorCode.DOOR_NOT_EXISTED)
        );
        if(!passwordEncoder.matches(password, door.getPassword())){
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }
        door.setUnlock(true);
        doorRepository.save(door);
    }

    public void lockDoor(){
        Door door = doorRepository.findById(doorId).orElseThrow(
                () -> new AppException(ErrorCode.DOOR_NOT_EXISTED)
        );
        door.setUnlock(false);
        doorRepository.save(door);
    }

    public void ChangePasswordDoor(UpdateDoorPasswordRequest request){
        if (request.getNewPassword().isEmpty() || request.getCurrentPassword().isEmpty()) throw new AppException(ErrorCode.EMPTY_FIELD);
        Door door = doorRepository.findById(doorId).orElseThrow(
                () -> new AppException(ErrorCode.DOOR_NOT_EXISTED)
        );
        boolean matches = passwordEncoder.matches(request.getCurrentPassword(), door.getPassword());
        if(!matches) throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        door.setPassword(passwordEncoder.encode(request.getNewPassword()));
        doorRepository.save(door);
    }

    public boolean stateDoor(){
        Door door = doorRepository.findById(doorId).orElseThrow(
                () -> new AppException(ErrorCode.DOOR_NOT_EXISTED)
        );
        return door.getUnlock();
    }
}
