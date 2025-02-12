package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.*;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.VerificationCodeResponse;
import com.example.UniLabPass.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;
    @PostMapping("/verify")
    CustomApiResponse<VerificationCodeResponse> authenticate(@RequestBody VerificationCodeRequest request) {
         return CustomApiResponse.<VerificationCodeResponse>builder()
                 .result(emailService.verifyCode(request))
                 .build();

    }

}
