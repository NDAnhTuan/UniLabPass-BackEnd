package com.example.UniLabPass.mapper;

import com.example.UniLabPass.dto.request.PermissionRequest;
import com.example.UniLabPass.dto.response.PermissionResponse;
import com.example.UniLabPass.entity.Permission;
import org.mapstruct.Mapper;
//import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
//    MyUserMapper INSTANCE = Mappers.getMapper(MyUserMapper.class);
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);

}
