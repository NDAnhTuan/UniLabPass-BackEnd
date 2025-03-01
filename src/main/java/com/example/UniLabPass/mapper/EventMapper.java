package com.example.UniLabPass.mapper;

import com.example.UniLabPass.dto.request.LabEventCreationRequest;
import com.example.UniLabPass.dto.request.LabEventUpdateRequest;
import com.example.UniLabPass.dto.response.LabEventRespond;
import com.example.UniLabPass.entity.LabEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {
    LabEvent toEvent(LabEventCreationRequest request);

    default LabEvent toEventUpdated(LabEventUpdateRequest request) {
        return LabEvent.builder()
                .id(request.getEventId())
                .labId(request.getLabId())
                .name(request.getName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
    };

    default LabEventRespond toEventRespond(LabEvent event) {
        return LabEventRespond.builder()
                .id(event.getId())
                .labId(event.getLabId())
                .name(event.getName())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .build();
    };
}
