package com.example.UniLabPass.service;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class ExpoPushService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final RestTemplate restTemplate;

    public ExpoPushService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendPushNotification(String expoPushToken, String title, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("Accept-Encoding", "gzip, deflate");

        Map<String, Object> message = new HashMap<>();
        message.put("to", expoPushToken);
        message.put("title", title);
        message.put("body", body);
        message.put("sound", "default"); // optional
        message.put("priority", "high"); // optional
        message.put("data", Map.of(
                "type", "yes-no",
                "question", "Do you want to accept the invite?"
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);

        System.out.println("Expo push response: " + response.getBody());
    }
}

