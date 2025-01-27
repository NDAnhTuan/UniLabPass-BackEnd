package com.example.UniLabPass.mapper;

import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.request.MyUserUpdateRequest;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.entity.MyUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
//import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MyUserMapper {
//    MyUserMapper INSTANCE = Mappers.getMapper(MyUserMapper.class);
    MyUser toMyUser(MyUserCreationRequest request);
    MyUserResponse toMyUserResponse(MyUser myUser);

    @Mapping(target = "roles", ignore = true)
    void updateMyUser(@MappingTarget MyUser myUser, MyUserUpdateRequest request);

}
