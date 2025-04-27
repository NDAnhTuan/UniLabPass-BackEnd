package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.response.ErrorApiResponse;
import com.example.UniLabPass.service.ExpoPushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final ExpoPushService fcmService;

    public NotificationController(ExpoPushService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping(value = "/send")
    @Operation(summary = "", security = {@SecurityRequirement(name = "BearerAuthentication")})

    public void sendNotification(@RequestParam String token,
                                   @RequestParam String title,
                                   @RequestParam String body) throws IOException {
        fcmService.sendPushNotification(token, title, body);
    }
}
