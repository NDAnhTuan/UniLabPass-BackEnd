package com.example.UniLabPass.mapper;

import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.request.MyUserUpdateRequest;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.entity.MyUser;
import org.mapstruct.*;
//import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MyUserMapper {
//    MyUserMapper INSTANCE = Mappers.getMapper(MyUserMapper.class);
    MyUser toMyUser(MyUserCreationRequest request);
    MyUserResponse toMyUserResponse(MyUser myUser);

    @Mapping(target = "roles", ignore = true)
    // Tự động bỏ các trường null của MyUserUpdateRequest
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMyUser(@MappingTarget MyUser myUser, MyUserUpdateRequest request);

}
