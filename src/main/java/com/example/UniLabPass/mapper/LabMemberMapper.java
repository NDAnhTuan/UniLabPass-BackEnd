package com.example.UniLabPass.mapper;

import com.example.UniLabPass.dto.request.LabMemberUpdateRequest;
import com.example.UniLabPass.dto.response.LabMemberResponse;
import com.example.UniLabPass.entity.LabMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface LabMemberMapper {
    default LabMemberResponse toLabMemberResponse(LabMember entity) {
        return LabMemberResponse.builder()
                .id(entity.getLabMemberId().getMyUserId())
                .firstName(entity.getMyUser().getFirstName())
                .lastName(entity.getMyUser().getLastName())
                .status(entity.getMemberStatus())
                .lastRecord(null) // Update when finish logs
                .build();
    }

    void updateLabMember(@MappingTarget LabMember labMember, LabMemberUpdateRequest request);
}
