package com.DADN.homeyolo.repository;

import com.DADN.homeyolo.entity.ActivityHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityHistoryRepository extends MongoRepository<ActivityHistory, String> {
}
