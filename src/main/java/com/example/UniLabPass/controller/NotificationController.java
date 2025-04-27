package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.NotificationResponse;
import com.example.UniLabPass.service.ExpoPushService;
import com.example.UniLabPass.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

     ExpoPushService expoPushService;
     NotificationService notificationService;

    @PostMapping(value = "/send")
    @Operation(summary = "", security = {@SecurityRequirement(name = "BearerAuthentication")})

    public CustomApiResponse<Void> sendNotification(@RequestParam String token,
                                              @RequestParam String title,
                                              @RequestParam String body) throws IOException {
        expoPushService.sendPushNotification(token, title, body);
        return CustomApiResponse.<Void>builder().build();
    }

    @GetMapping(value = "")
    @Operation(summary = "", security = {@SecurityRequirement(name = "BearerAuthentication")})

    public CustomApiResponse<List<NotificationResponse>> getMyNotifications()  {
        return CustomApiResponse.<List<NotificationResponse>>builder()
                .result(notificationService.getMyNotifications())
                .build();
    }

    @DeleteMapping
    @Operation(summary = "", security = {@SecurityRequirement(name = "BearerAuthentication")})
    public CustomApiResponse<Void> deleteNotification(@PathVariable("id") String id) {
        return CustomApiResponse.<Void>builder().build();
    }
}
