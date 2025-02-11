package com.example.UniLabPass.service;

import com.example.UniLabPass.dto.request.VerificationCodeRequest;
import com.example.UniLabPass.dto.response.VerificationCodeResponse;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.repository.MyUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {
    JavaMailSender mailSender;
    MyUserRepository myUserRepository;


    public void sendVerificationEmail(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Email Verification Code");
        message.setText("Your verification code is: " + verificationCode
                + "\n Your verification code only lasts for 5 minutes, please enter it quickly");
        mailSender.send(message);
    }
    // Hàm verify email
    public VerificationCodeResponse verifyCode(VerificationCodeRequest request) {
        MyUser myUser = myUserRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        var verifiedEmail = true;
        if (!myUser.getExpiryVerificationCode().after(new Date())
                || !myUser.getVerificationCode().equals(request.getCode())) {
            verifiedEmail = false;
        }
        myUser.setVerified(verifiedEmail);
        myUserRepository.save(myUser);
        return VerificationCodeResponse.builder()
                .verifiedEmail(verifiedEmail)
                .build();
    }

}
