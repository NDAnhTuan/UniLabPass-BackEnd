package com.example.UniLabPass.mapper;

import com.example.UniLabPass.dto.request.MyUserUpdateRequest;
import com.example.UniLabPass.dto.response.NotificationResponse;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.entity.Notification;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toNotificationResponse(Notification notification);
}
