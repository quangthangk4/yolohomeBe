package com.DADN.homeyolo.service;

import com.DADN.homeyolo.dto.response.ActivityHistoryResponse;
import com.DADN.homeyolo.entity.ActivityHistory;
import com.DADN.homeyolo.mapper.ActivityHistoryMapper;
import com.DADN.homeyolo.repository.ActivityHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityHistoryService {

    private final ActivityHistoryRepository activityHistoryRepository;
    private final ActivityHistoryMapper activityHistoryMapper;

    public List<ActivityHistoryResponse> activityHistory(){
        List<ActivityHistory> listHistory = activityHistoryRepository.findAll();
        return listHistory.stream().map(activityHistoryMapper::toActivityHistoryResponse)
                .collect(Collectors.toList());
    }
}
