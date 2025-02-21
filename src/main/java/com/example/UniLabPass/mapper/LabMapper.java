package com.example.UniLabPass.mapper;

import com.example.UniLabPass.dto.request.LabCreationRequest;
import com.example.UniLabPass.dto.request.LabUpdateRequest;
import com.example.UniLabPass.dto.response.LabResponse;
import com.example.UniLabPass.entity.Lab;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LabMapper {
    Lab toLab(LabCreationRequest request);
    LabResponse toLabResponse(Lab lab);

    void updateLab(@MappingTarget Lab lab, LabUpdateRequest request);
}
