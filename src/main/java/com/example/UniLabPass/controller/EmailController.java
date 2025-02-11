package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.*;
import com.example.UniLabPass.dto.response.ApiResponse;
import com.example.UniLabPass.dto.response.AuthenticationResponse;
import com.example.UniLabPass.dto.response.IntrospectResponse;
import com.example.UniLabPass.dto.response.VerificationCodeResponse;
import com.example.UniLabPass.service.AuthenticationService;
import com.example.UniLabPass.service.EmailService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;
    @PostMapping("/verify")
    ApiResponse<VerificationCodeResponse> authenticate(@RequestBody VerificationCodeRequest request) {
         return ApiResponse.<VerificationCodeResponse>builder()
                 .result(emailService.verifyCode(request))
                 .build();

    }

}
