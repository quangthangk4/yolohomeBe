package com.DADN.homeyolo.repository.httpClient;

import com.DADN.homeyolo.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByEmail(String email);
}
