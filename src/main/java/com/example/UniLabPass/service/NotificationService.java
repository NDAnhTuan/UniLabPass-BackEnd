package com.example.UniLabPass.service;

import com.example.UniLabPass.dto.response.NotificationResponse;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.entity.Notification;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.NotificationMapper;
import com.example.UniLabPass.repository.MyUserRepository;
import com.example.UniLabPass.repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    MyUserRepository myUserRepository;

    public List<NotificationResponse> getMyNotifications() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        MyUser myUser = myUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<Notification> notifications = notificationRepository.findAllByUserId(myUser.getId());
        return notifications.stream().map(
                notification -> notificationMapper.toNotificationResponse(notification)
        ).toList();
    }

    public void deleteNotification(String id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.NOTIFICATION_NOT_EXIST)
        );
        MyUser myUser = myUserRepository.findById(notification.getUserId()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        if (!myUser.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        notificationRepository.delete(notification);
    }
}
