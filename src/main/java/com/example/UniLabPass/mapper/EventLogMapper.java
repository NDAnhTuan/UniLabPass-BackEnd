package com.example.UniLabPass.mapper;

import com.example.UniLabPass.dto.request.EventLogCreationRequest;
import com.example.UniLabPass.dto.response.EventLogRespond;
import com.example.UniLabPass.entity.EventLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventLogMapper {
    EventLog toEventLog(EventLogCreationRequest request);
    EventLogRespond toEventLogRespond(EventLog eventLog);
}
