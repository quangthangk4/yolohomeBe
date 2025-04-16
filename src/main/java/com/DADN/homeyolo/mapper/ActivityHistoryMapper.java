package com.DADN.homeyolo.mapper;

import com.DADN.homeyolo.dto.response.ActivityHistoryResponse;
import com.DADN.homeyolo.entity.ActivityHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityHistoryMapper {

    ActivityHistoryResponse toActivityHistoryResponse(ActivityHistory activityHistory);
}
