package com.example.UniLabPass.mapper;

import com.example.UniLabPass.dto.request.LogCreationRequest;
import com.example.UniLabPass.dto.response.LogDetailRespond;
import com.example.UniLabPass.dto.response.LogRespond;
import com.example.UniLabPass.entity.LaboratoryLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LogMapper {
    LogRespond toLogRespond(LaboratoryLog log);
    LaboratoryLog toLaboratoryLog(LogCreationRequest request);
    LogDetailRespond toLogDetailRespond(LaboratoryLog log);
}
