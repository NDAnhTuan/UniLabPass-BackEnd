package com.example.UniLabPass.service;

import com.example.UniLabPass.dto.request.ResendVerificationCodeRequest;
import com.example.UniLabPass.dto.request.VerificationCodeRequest;
import com.example.UniLabPass.dto.response.VerificationCodeResponse;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.repository.MyUserRepository;
import com.example.UniLabPass.utils.GlobalUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {
    JavaMailSender mailSender;
    MyUserRepository myUserRepository;
    PasswordEncoder passwordEncoder;

    GlobalUtils globalUtils;


    public void sendVerificationEmail(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Email Verification Code");
        message.setText("Your verification code is: " + verificationCode
                + "\n Your verification code only lasts for 5 minutes, please enter it quickly");
        mailSender.send(message);
    }

    public void sendResetPassword(String email) {
        String newPass = globalUtils.generateVerificationCode() + globalUtils.generateVerificationCode();
        MyUser myUser = myUserRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        if (!myUser.isVerified()) throw new AppException(ErrorCode.UNVERIFIED_EMAIL);
        myUser.setPassword(passwordEncoder.encode(newPass));
        myUserRepository.save(myUser);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("Your Reset Password is: " + newPass
                + "\n This is a your new password");
        mailSender.send(message);
    }
    // Hàm verify email
    public VerificationCodeResponse verifyCode(VerificationCodeRequest request) {
        MyUser myUser = myUserRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        if (!myUser.getExpiryVerificationCode().after(new Date())
                || !myUser.getVerificationCode().equals(request.getCode()))
            throw new AppException(ErrorCode.INCORRECT_VERIFY_CODE);
        myUser.setVerified(true);
        myUserRepository.save(myUser);
        return VerificationCodeResponse.builder()
                .verifiedEmail(true)
                .build();
    }

    public void resendVerifyCode(ResendVerificationCodeRequest request) {
        MyUser myUser = myUserRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        myUser.setVerificationCode(globalUtils.generateVerificationCode());
        myUser.setExpiryVerificationCode(new Date(
                Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli()
        ));
        myUserRepository.save(myUser);
        sendVerificationEmail(myUser.getEmail(), myUser.getVerificationCode());
    }


}
