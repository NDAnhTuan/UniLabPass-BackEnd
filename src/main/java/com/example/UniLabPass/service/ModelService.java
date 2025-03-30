package com.example.UniLabPass.service;

import com.example.UniLabPass.repository.httpClient.ModelClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ModelService {
    ModelClient modelClient;

    public Object verify(MultipartFile image1, MultipartFile image2) {
        return modelClient.verify(image1,image2);
    }

    public String healthcheck() {
        return modelClient.healthcheck();
    }
}
