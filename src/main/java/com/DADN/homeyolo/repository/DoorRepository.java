package com.DADN.homeyolo.repository;

import com.DADN.homeyolo.entity.Door;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoorRepository extends MongoRepository<Door, String> {
}
