package com.DADN.homeyolo.repository;

import com.DADN.homeyolo.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByEmail(String email);

    Optional<Object> findByUsername(String username);
}
