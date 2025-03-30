package com.example.UniLabPass.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "model-service", url = "${app.services.model}")
public interface ModelClient {
    @PostMapping(value = "/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Object verify(@RequestPart("image1") MultipartFile image1,
                         @RequestPart("image2") MultipartFile image2);
    @GetMapping(value = "/healthcheck", produces = MediaType.APPLICATION_JSON_VALUE)
    String healthcheck();
}
