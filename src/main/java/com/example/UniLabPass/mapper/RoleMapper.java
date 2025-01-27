package com.example.UniLabPass.mapper;

import com.example.UniLabPass.dto.request.RoleRequest;
import com.example.UniLabPass.dto.response.RoleResponse;
import com.example.UniLabPass.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    // Bỏ qua permissions (vì req là string ,entity là obj)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);

}
