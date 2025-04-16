package com.DADN.homeyolo.repository.httpClient;

import com.DADN.homeyolo.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
