package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.ErrorApiResponse;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.enums.Role;
import com.example.UniLabPass.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/model")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ModelController {
    ModelService modelService;

    @Operation(summary = "Verify", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @PostMapping(value = "/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    CustomApiResponse<Object> verify(@RequestPart("image1") MultipartFile image1,
                                     @RequestPart("userId") String userId) throws IOException {
        return CustomApiResponse.<Object>builder()
                .result(modelService.verify(image1,userId))
                .build();
    }

    @Operation(summary = "healthcheck", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @PostMapping(value = "/healthcheck")
    CustomApiResponse<String> healthcheck()  {
        return CustomApiResponse.<String>builder()
                .result(modelService.healthcheck())
                .build();
    }
}
