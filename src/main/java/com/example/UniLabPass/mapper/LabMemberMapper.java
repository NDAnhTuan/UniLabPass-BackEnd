package com.example.UniLabPass.mapper;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LabCreationRequest;
import com.example.UniLabPass.dto.request.LabMemberUpdateRequest;
import com.example.UniLabPass.dto.request.LabUpdateRequest;
import com.example.UniLabPass.dto.response.LabMemberResponse;
import com.example.UniLabPass.entity.Lab;
import com.example.UniLabPass.entity.LabMember;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LabMemberMapper {
    LabMemberResponse toLabMemberResponse(LabMember labMember);

    void updateLabMember(@MappingTarget LabMember labMember, LabMemberUpdateRequest request);
}
