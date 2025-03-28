package com.example.UniLabPass.controller;
import com.example.UniLabPass.dto.response.CloudinaryResponse;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
@Slf4j
public class FileUploadController {

    private final CloudinaryService cloudinaryService;

    public FileUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping(value = "/upload/user", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "", security = {@SecurityRequirement(name = "BearerAuthentication")})
    public CustomApiResponse<CloudinaryResponse> uploadFileMyUser(
            @RequestPart("file") MultipartFile file,
            @RequestPart("userId") String userId,
            @RequestPart(value = "labId", required = false) String labId
    ) throws IOException {
        return CustomApiResponse.<CloudinaryResponse>builder()
                .result(cloudinaryService.uploadFileMyUser(userId,labId,file))
                .build();
    }

    @PostMapping(value = "/upload/log", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "", security = {@SecurityRequirement(name = "BearerAuthentication")})
    public CustomApiResponse<CloudinaryResponse> uploadFileLogEvent (
            @RequestPart("file") MultipartFile file,
            @RequestPart("logId") String logId,
            @RequestPart("LogType") @Schema(example = "Normal/Event") String logType
    ) throws IOException {
        return CustomApiResponse.<CloudinaryResponse>builder()
                .result(cloudinaryService.uploadFileLog(logId,file, logType))
                .build();
    }

    @DeleteMapping(value = "/delete/{userId}")
    @Operation(summary = "", security = {@SecurityRequirement(name = "BearerAuthentication")})
    public CustomApiResponse<Void> deleteFileUser(@PathVariable String userId) throws IOException {
        cloudinaryService.deleteFileUser(userId);
        return CustomApiResponse.<Void>builder()
                .build();
    }

    @DeleteMapping(value = "/delete/{logId}")
    @Operation(summary = "", security = {@SecurityRequirement(name = "BearerAuthentication")})
    public CustomApiResponse<Void> deleteFileLog(@PathVariable String logId) throws IOException {
        cloudinaryService.deleteFileLog(logId);
        return CustomApiResponse.<Void>builder()
                .build();
    }

}