package com.example.UniLabPass.mapper;

import com.example.UniLabPass.dto.response.LabMemberResponse;
import com.example.UniLabPass.entity.LabMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface LabMemberMapper {
    @Mapping(target = "role", ignore = true)

    LabMemberResponse toLabMemberResponse(LabMember labMember);
}
